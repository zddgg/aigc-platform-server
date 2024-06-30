package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.vavr.Tuple2;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import space.wenliang.ai.aigcplatformserver.ai.chat.AiService;
import space.wenliang.ai.aigcplatformserver.bean.AiResult;
import space.wenliang.ai.aigcplatformserver.bean.ChapterExpose;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.*;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.AudioUtils;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.SubtitleUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BTextChapterServiceImpl implements BTextChapterService {

    private final AiService aiService;
    private final ATextRoleService aTextRoleService;
    private final ATextChapterService aTextChapterService;
    private final ATextProjectService aTextProjectService;
    private final AChapterInfoService aChapterInfoService;
    private final ARoleInferenceService aRoleInferenceService;
    private final ATextCommonRoleService aTextCommonRoleService;

    private final ARefAudioService aRefAudioService;
    private final AGptSovitsModelService aGptSovitsModelService;
    private final AGptSovitsConfigService aGptSovitsConfigService;
    private final AFishSpeechModelService aFishSpeechModelService;
    private final AFishSpeechConfigService aFishSpeechConfigService;
    private final AChatTtsConfigService aChatTtsConfigService;
    private final AEdgeTtsConfigService aEdgeTtsConfigService;
    private final PathConfig pathConfig;
    private final GlobalWebSocketHandler globalWebSocketHandler;

    public BTextChapterServiceImpl(AiService aiService,
                                   ATextRoleService aTextRoleService,
                                   ATextChapterService aTextChapterService,
                                   ATextProjectService aTextProjectService,
                                   AChapterInfoService aChapterInfoService,
                                   ARoleInferenceService aRoleInferenceService,
                                   ATextCommonRoleService aTextCommonRoleService,
                                   ARefAudioService aRefAudioService,
                                   AGptSovitsModelService aGptSovitsModelService,
                                   AGptSovitsConfigService aGptSovitsConfigService,
                                   AFishSpeechModelService aFishSpeechModelService,
                                   AFishSpeechConfigService aFishSpeechConfigService,
                                   AChatTtsConfigService aChatTtsConfigService,
                                   AEdgeTtsConfigService aEdgeTtsConfigService,
                                   PathConfig pathConfig,
                                   GlobalWebSocketHandler globalWebSocketHandler) {
        this.aiService = aiService;
        this.aTextRoleService = aTextRoleService;
        this.aTextChapterService = aTextChapterService;
        this.aTextProjectService = aTextProjectService;
        this.aChapterInfoService = aChapterInfoService;
        this.aRoleInferenceService = aRoleInferenceService;
        this.aTextCommonRoleService = aTextCommonRoleService;
        this.aRefAudioService = aRefAudioService;
        this.aGptSovitsModelService = aGptSovitsModelService;
        this.aGptSovitsConfigService = aGptSovitsConfigService;
        this.aFishSpeechModelService = aFishSpeechModelService;
        this.aFishSpeechConfigService = aFishSpeechConfigService;
        this.aChatTtsConfigService = aChatTtsConfigService;
        this.aEdgeTtsConfigService = aEdgeTtsConfigService;
        this.pathConfig = pathConfig;
        this.globalWebSocketHandler = globalWebSocketHandler;
    }

    @Override
    public List<TextChapterEntity> chapters(String projectId) {
        List<TextChapterEntity> chapterEntities = aTextChapterService.list(projectId);
        if (!CollectionUtils.isEmpty(chapterEntities)) {
            Map<String, Long> chapterTextCount = aChapterInfoService.chapterGroupCount();
            Map<String, Boolean> chapterExportCount = aChapterInfoService.chapterExportCount();
            Map<String, Long> chapterRoleCount = aTextRoleService.chapterGroupCount();

            chapterEntities = chapterEntities.stream()
                    .peek(t -> {
                        t.setTextNum(chapterTextCount.get(t.getChapterId()));
                        t.setRoleNum(chapterRoleCount.get(t.getChapterId()));
                        if (chapterExportCount.containsKey(t.getChapterId())) {
                            t.setStage("处理中");
                            if (Objects.equals(chapterExportCount.get(t.getChapterId()), Boolean.TRUE)) {
                                t.setStage("合并完成");
                            }
                        }
                    })
                    .toList();
        }
        return chapterEntities;
    }

    @Override
    public List<ChapterInfoEntity> tmpDialogueParse(String projectId,
                                                    String chapterId,
                                                    String dialoguePattern,
                                                    String textContent) {
        List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

        if (StringUtils.isNotBlank(textContent)) {
            List<String> dialoguePatterns = StringUtils.isBlank(dialoguePattern) ? List.of() : List.of(dialoguePattern);

            for (String line : textContent.split("\n")) {
                List<Tuple2<Boolean, List<String>>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

                for (Tuple2<Boolean, List<String>> chapterInfoTuple2 : chapterInfoTuple2s) {

                    for (int i = 0; i < chapterInfoTuple2._2.size(); i++) {

                        if (Objects.equals(chapterInfoTuple2._1, Boolean.TRUE)) {
                            ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                            chapterInfoEntity.setText(chapterInfoTuple2._2.get(i));
                            chapterInfoEntities.add(chapterInfoEntity);
                        }
                    }
                }
            }
        }

        return chapterInfoEntities;
    }

    @Override
    public void dialogueParse(String projectId,
                              String chapterId,
                              String dialoguePattern,
                              String textContent) {
        TextChapterEntity entity = aTextChapterService.getOne(projectId, chapterId);
        if (Objects.isNull(entity)) {
            throw new BizException("章节不存在");
        }

        if (StringUtils.isNotBlank(textContent)) {
            List<String> dialoguePatterns = StringUtils.isBlank(dialoguePattern) ? List.of() : List.of(dialoguePattern);

            List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

            int paragraphIndex = 0;

            for (String line : textContent.split("\n")) {
                int splitIndex = 0;

                List<Tuple2<Boolean, List<String>>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

                for (Tuple2<Boolean, List<String>> chapterInfoTuple2 : chapterInfoTuple2s) {

                    for (int i = 0; i < chapterInfoTuple2._2.size(); i++) {
                        ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                        chapterInfoEntity.setProjectId(projectId);
                        chapterInfoEntity.setChapterId(chapterId);
                        chapterInfoEntity.setParagraphIndex(paragraphIndex);
                        chapterInfoEntity.setSplitIndex(splitIndex);
                        chapterInfoEntity.setSentenceIndex(i);
                        chapterInfoEntity.setText(chapterInfoTuple2._2.get(i));
                        chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._1);

                        chapterInfoEntity.setRole("旁白");

                        chapterInfoEntities.add(chapterInfoEntity);
                    }

                    splitIndex++;
                }

                paragraphIndex++;
            }

            aChapterInfoService.deleteByProjectIdAndChapterId(projectId, chapterId);
            aTextRoleService.delete(projectId, chapterId);

            if (!CollectionUtils.isEmpty(chapterInfoEntities)) {

                List<TextCommonRoleEntity> commonRoleEntities = aTextCommonRoleService.list(projectId);
                Optional<TextCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                        .filter(r -> StringUtils.equals(r.getRole(), "旁白"))
                        .findAny();

                TextRoleEntity textRoleEntity = new TextRoleEntity();
                textRoleEntity.setProjectId(projectId);
                textRoleEntity.setChapterId(chapterId);
                textRoleEntity.setRole("旁白");

                if (asideRoleOptional.isPresent()) {
                    TextCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();

                    textRoleEntity.setFromCommonRole(textCommonRoleEntity);

                    chapterInfoEntities = chapterInfoEntities.stream()
                            .peek(c -> {
                                c.setAudioModelType(textCommonRoleEntity.getAudioModelType());
                                c.setAudioModelId(textCommonRoleEntity.getAudioModelId());
                                c.setAudioConfigId(textCommonRoleEntity.getAudioConfigId());
                                c.setRefAudioId(textCommonRoleEntity.getRefAudioId());
                            }).toList();
                }

                aChapterInfoService.deleteByProjectIdAndChapterId(projectId, chapterId);
                aChapterInfoService.saveBatch(chapterInfoEntities);
                aTextRoleService.save(textRoleEntity);

            }
        }
    }

    @Override
    public Flux<String> roleInference(String projectId, String chapterId) {

        List<ChapterInfoEntity> chapterInfos = aChapterInfoService.list(projectId, chapterId);

        List<JSONObject> linesList = new ArrayList<>();

        Map<Integer, Map<Integer, List<ChapterInfoEntity>>> groups = chapterInfos
                .stream()
                .filter(v -> Objects.equals(v.getDialogueFlag(), Boolean.TRUE))
                .collect(Collectors.groupingBy(
                        ChapterInfoEntity::getParagraphIndex,
                        TreeMap::new,
                        Collectors.groupingBy(
                                ChapterInfoEntity::getSplitIndex,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ));

        groups.forEach((_, value1) -> value1.forEach((_, value) -> {
            List<ChapterInfoEntity> entities = value.stream()
                    .sorted(Comparator.comparingInt(ChapterInfoEntity::getSentenceIndex))
                    .toList();
            String index = entities
                    .stream()
                    .map(ChapterInfoEntity::getSecondIndex)
                    .distinct()
                    .collect(Collectors.joining(","));
            String text = entities
                    .stream()
                    .map(ChapterInfoEntity::getText)
                    .collect(Collectors.joining());
            JSONObject lines = new JSONObject();
            lines.put("index", index);
            lines.put("lines", text);
            linesList.add(lines);
        }));

        if (CollectionUtils.isEmpty(linesList)) {
            return Flux.empty();
        }

        String lines = JSON.toJSONString(linesList);
        StringBuilder content = new StringBuilder();

        chapterInfos.stream()
                .collect(Collectors.groupingBy(ChapterInfoEntity::getParagraphIndex, TreeMap::new, Collectors.toList()))
                .values()
                .forEach(val -> {
                    val.stream().sorted(Comparator.comparingInt(ChapterInfoEntity::getSplitIndex)
                                    .thenComparingInt(ChapterInfoEntity::getSentenceIndex))
                            .map(ChapterInfoEntity::getText)
                            .forEach(content::append);
                    content.append("\n");
                });


        String systemMessage = "你是一个小说内容台词分析员，你会精确的找到台词在原文中的位置并分析属于哪个角色，以及角色在说这句台词时的上下文环境及情绪等。";

        String userMessage = STR."""
                严格按照以下要求工作：
                \{parseStep}

                输出格式如下：
                \{outputFormat}

                台词部分：
                \{lines}

                原文部分：
                \{content.toString()}
                """;

        log.info("\n提示词, systemMessage: {}", systemMessage);
        log.info("\n提示词, userMessage: {}", userMessage);

        StringBuilder aiResultStr = new StringBuilder();

        return aiService.stream(systemMessage, userMessage)

//        String longString = temp;
//        int charactersPerSecond = 80;
//
//        return Flux.interval(Duration.ofMillis(200))
//                .map(i -> longString.substring((int) Math.min((i * charactersPerSecond), longString.length()), (int) Math.min((i + 1) * charactersPerSecond, longString.length())))
//                .takeWhile(s -> !s.isEmpty())


                .publishOn(Schedulers.boundedElastic())
                .doOnNext(v -> {
                    System.out.println(v);
                    aiResultStr.append(v);
                })
                .doOnComplete(() -> mergeAiResultInfo(projectId, chapterId, aiResultStr.toString(), chapterInfos));
    }

    @Override
    public Boolean checkRoleInference(String projectId, String chapterId) {
        List<RoleInferenceEntity> list = aRoleInferenceService.list(projectId, chapterId);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public void loadRoleInference(String projectId, String chapterId) {
        List<RoleInferenceEntity> roleInferenceEntities = aRoleInferenceService.list(projectId, chapterId);

        if (!CollectionUtils.isEmpty(roleInferenceEntities)) {
            List<TextCommonRoleEntity> commonRoles = aTextCommonRoleService.list();
            Map<String, TextCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(TextCommonRoleEntity::getRole, Function.identity(), (a, _) -> a));

            List<TextRoleEntity> textRoleEntities = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(RoleInferenceEntity::getRole, Function.identity(), (v1, _) -> v1))
                    .values()
                    .stream().map(roleInferenceEntity -> {
                        TextRoleEntity textRoleEntity = new TextRoleEntity();
                        textRoleEntity.setProjectId(projectId);
                        textRoleEntity.setChapterId(chapterId);
                        textRoleEntity.setRole(roleInferenceEntity.getRole());
                        textRoleEntity.setGender(roleInferenceEntity.getGender());
                        textRoleEntity.setAgeGroup(roleInferenceEntity.getAgeGroup());
                        TextCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());
                        if (Objects.nonNull(commonRole)) {
                            if (Objects.isNull(textRoleEntity.getGender())) {
                                textRoleEntity.setGender(commonRole.getGender());
                            }
                            if (Objects.isNull(textRoleEntity.getAgeGroup())) {
                                textRoleEntity.setAgeGroup(commonRole.getAgeGroup());
                            }
                            textRoleEntity.setAudioModelType(commonRole.getAudioModelType());
                            textRoleEntity.setAudioModelId(commonRole.getAudioModelId());
                            textRoleEntity.setAudioConfigId(commonRole.getAudioConfigId());
                            textRoleEntity.setRefAudioId(commonRole.getRefAudioId());
                        }
                        return textRoleEntity;
                    }).toList();

            Map<String, RoleInferenceEntity> roleInferenceEntityMap = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(RoleInferenceEntity::getTextIndex, Function.identity(), (a, _) -> a));

            List<ChapterInfoEntity> chapterInfoEntities = aChapterInfoService.list(projectId, chapterId);

            List<Integer> audioModelResetIds = new ArrayList<>();

            String asideRole = "旁白";

            Optional<TextRoleEntity> hasAsideRole = textRoleEntities
                    .stream()
                    .filter(c -> StringUtils.equals(c.getRole(), asideRole))
                    .findFirst();

            Optional<ChapterInfoEntity> hasAsideText = chapterInfoEntities
                    .stream()
                    .filter(c -> StringUtils.equals(c.getRole(), asideRole))
                    .findFirst();

            List<ChapterInfoEntity> saveInfos = chapterInfoEntities.stream()
                    .filter(c -> roleInferenceEntityMap.containsKey(c.getSecondIndex()))
                    .peek(c -> {
                        RoleInferenceEntity roleInferenceEntity = roleInferenceEntityMap.get(c.getSecondIndex());
                        c.setRole(roleInferenceEntity.getRole());

                        if (commonRoleMap.containsKey(roleInferenceEntity.getRole())) {
                            TextCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());
                            c.setAudioModelType(commonRole.getAudioModelType());
                            c.setAudioModelId(commonRole.getAudioModelId());
                            c.setAudioConfigId(commonRole.getAudioConfigId());
                            c.setRefAudioId(commonRole.getRefAudioId());
                        } else {
                            audioModelResetIds.add(c.getId());
                        }
                    }).toList();

            aChapterInfoService.updateBatchById(saveInfos);
            aChapterInfoService.audioModelReset(audioModelResetIds);

            ArrayList<TextRoleEntity> saveTextRoles = new ArrayList<>(textRoleEntities);

            if (hasAsideRole.isEmpty() && hasAsideText.isPresent()) {

                TextRoleEntity textRoleEntity = new TextRoleEntity();
                textRoleEntity.setProjectId(projectId);
                textRoleEntity.setChapterId(chapterId);
                textRoleEntity.setRole(asideRole);
                TextCommonRoleEntity commonRole = commonRoleMap.get(asideRole);
                if (Objects.nonNull(commonRole)) {
                    textRoleEntity.setAudioModelType(commonRole.getAudioModelType());
                    textRoleEntity.setAudioModelId(commonRole.getAudioModelId());
                    textRoleEntity.setAudioConfigId(commonRole.getAudioConfigId());
                    textRoleEntity.setRefAudioId(commonRole.getRefAudioId());
                }
                saveTextRoles.add(textRoleEntity);
            }

            aTextRoleService.delete(projectId, chapterId);
            aTextRoleService.saveBatch(saveTextRoles);
        }
    }

    @Override
    public String getContent(String projectId, String chapterId) {
        return aTextChapterService.getContent(projectId, chapterId);
    }

    @Override
    public List<TextRoleEntity> roles(String projectId, String chapterId) {
        List<TextRoleEntity> roleEntities = aTextRoleService.list(projectId, chapterId);
        if (!CollectionUtils.isEmpty(roleEntities)) {
            Map<String, Long> roleCountMap = aChapterInfoService.list(projectId, chapterId)
                    .stream()
                    .collect(Collectors.groupingBy(ChapterInfoEntity::getRole, Collectors.counting()));

            List<TextRoleEntity> deleteList = new ArrayList<>();

            roleEntities = roleEntities.stream()
                    .peek(r -> r.setRoleCount(Optional.ofNullable(roleCountMap.get(r.getRole())).orElse(0L)))
                    .filter(r -> {
                        if (r.getRoleCount() > 0) {
                            return true;
                        } else {
                            deleteList.add(r);
                            return false;
                        }
                    })
                    .toList();

            if (!CollectionUtils.isEmpty(deleteList)) {
                CompletableFuture.runAsync(() -> aTextRoleService.removeByIds(deleteList.stream().map(TextRoleEntity::getId).toList()));
            }
        }

        if (!CollectionUtils.isEmpty(roleEntities)) {
            Map<String, RefAudioEntity> refAudioEntityMap = aRefAudioService.list()
                    .stream()
                    .collect(Collectors.toMap(RefAudioEntity::getRefAudioId, Function.identity()));
            Map<String, GptSovitsModelEntity> gptSovitsModelEntityMap = aGptSovitsModelService.list()
                    .stream()
                    .collect(Collectors.toMap(GptSovitsModelEntity::getModelId, Function.identity()));
            Map<String, GptSovitsConfigEntity> gptSovitsConfigEntityMap = aGptSovitsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(GptSovitsConfigEntity::getConfigId, Function.identity()));
            Map<String, FishSpeechModelEntity> fishSpeechModelEntityMap = aFishSpeechModelService.list()
                    .stream()
                    .collect(Collectors.toMap(FishSpeechModelEntity::getModelId, Function.identity()));
            Map<String, FishSpeechConfigEntity> fishSpeechConfigEntityMap = aFishSpeechConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(FishSpeechConfigEntity::getConfigId, Function.identity()));
            Map<String, ChatTtsConfigEntity> chatTtsConfigEntityMap = aChatTtsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(ChatTtsConfigEntity::getConfigId, Function.identity()));
            Map<String, EdgeTtsConfigEntity> edgeTtsConfigEntityMap = aEdgeTtsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(EdgeTtsConfigEntity::getConfigId, Function.identity()));

            roleEntities = roleEntities
                    .stream()
                    .peek(e -> {
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.gpt_sovits.getName())) {
                            e.setGptSovitsModel(gptSovitsModelEntityMap.get(e.getAudioModelId()));
                            e.setGptSovitsConfig(gptSovitsConfigEntityMap.get(e.getAudioConfigId()));
                            e.setRefAudio(refAudioEntityMap.get(e.getRefAudioId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.fish_speech.getName())) {
                            e.setFishSpeechModel(fishSpeechModelEntityMap.get(e.getAudioModelId()));
                            e.setFishSpeechConfig(fishSpeechConfigEntityMap.get(e.getAudioConfigId()));
                            e.setRefAudio(refAudioEntityMap.get(e.getRefAudioId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.chat_tts.getName())) {
                            e.setChatTtsConfig(chatTtsConfigEntityMap.get(e.getAudioConfigId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.edge_tts.getName())) {
                            e.setEdgeTtsConfig(edgeTtsConfigEntityMap.get(e.getAudioConfigId()));
                        }

                    })
                    .toList();
        }

        return roleEntities;
    }

    @Override
    public List<TextCommonRoleEntity> commonRoles(String projectId) {

        List<TextCommonRoleEntity> commonRoleEntities = aTextCommonRoleService.list(projectId);

        if (!CollectionUtils.isEmpty(commonRoleEntities)) {

            Map<String, RefAudioEntity> refAudioEntityMap = aRefAudioService.list()
                    .stream()
                    .collect(Collectors.toMap(RefAudioEntity::getRefAudioId, Function.identity()));
            Map<String, GptSovitsModelEntity> gptSovitsModelEntityMap = aGptSovitsModelService.list()
                    .stream()
                    .collect(Collectors.toMap(GptSovitsModelEntity::getModelId, Function.identity()));
            Map<String, GptSovitsConfigEntity> gptSovitsConfigEntityMap = aGptSovitsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(GptSovitsConfigEntity::getConfigId, Function.identity()));
            Map<String, FishSpeechModelEntity> fishSpeechModelEntityMap = aFishSpeechModelService.list()
                    .stream()
                    .collect(Collectors.toMap(FishSpeechModelEntity::getModelId, Function.identity()));
            Map<String, FishSpeechConfigEntity> fishSpeechConfigEntityMap = aFishSpeechConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(FishSpeechConfigEntity::getConfigId, Function.identity()));
            Map<String, ChatTtsConfigEntity> chatTtsConfigEntityMap = aChatTtsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(ChatTtsConfigEntity::getConfigId, Function.identity()));
            Map<String, EdgeTtsConfigEntity> edgeTtsConfigEntityMap = aEdgeTtsConfigService.list()
                    .stream()
                    .collect(Collectors.toMap(EdgeTtsConfigEntity::getConfigId, Function.identity()));

            commonRoleEntities = commonRoleEntities
                    .stream()
                    .peek(e -> {
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.gpt_sovits.getName())) {
                            e.setGptSovitsModel(gptSovitsModelEntityMap.get(e.getAudioModelId()));
                            e.setGptSovitsConfig(gptSovitsConfigEntityMap.get(e.getAudioConfigId()));
                            e.setRefAudio(refAudioEntityMap.get(e.getRefAudioId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.fish_speech.getName())) {
                            e.setFishSpeechModel(fishSpeechModelEntityMap.get(e.getAudioModelId()));
                            e.setFishSpeechConfig(fishSpeechConfigEntityMap.get(e.getAudioConfigId()));
                            e.setRefAudio(refAudioEntityMap.get(e.getRefAudioId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.chat_tts.getName())) {
                            e.setChatTtsConfig(chatTtsConfigEntityMap.get(e.getAudioConfigId()));
                        }
                        if (StringUtils.equals(e.getAudioModelType(), ModelTypeEnum.edge_tts.getName())) {
                            e.setEdgeTtsConfig(edgeTtsConfigEntityMap.get(e.getAudioConfigId()));
                        }

                    })
                    .toList();
        }

        return commonRoleEntities;
    }

    @Override
    public void createCommonRole(TextCommonRoleEntity textCommonRoleEntity) {
        aTextCommonRoleService.list(textCommonRoleEntity.getProjectId())
                .stream()
                .filter(r -> StringUtils.equals(r.getRole(), textCommonRoleEntity.getRole()))
                .findAny()
                .ifPresent(r -> {
                    throw new BizException("预置角色名称[" + r.getRole() + "]已存在");
                });

        aTextCommonRoleService.save(textCommonRoleEntity);
    }

    @Override
    public void updateCommonRole(TextCommonRoleEntity textCommonRoleEntity) {
        aTextCommonRoleService.updateById(textCommonRoleEntity);
    }

    @Override
    public void deleteCommonRole(TextCommonRoleEntity textCommonRoleEntity) {
        aTextCommonRoleService.removeById(textCommonRoleEntity);
    }

    @Override
    public void updateRoleName(TextRoleEntity textRoleEntity) {
        TextRoleEntity cache = aTextRoleService.getById(textRoleEntity.getId());

        aTextRoleService.updateById(textRoleEntity);

        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getRole, textRoleEntity.getRole())
                .eq(ChapterInfoEntity::getProjectId, textRoleEntity.getProjectId())
                .eq(ChapterInfoEntity::getChapterId, textRoleEntity.getChapterId())
                .eq(ChapterInfoEntity::getRole, cache.getRole()));
    }

    @Override
    public void updateRole(TextRoleEntity textRoleEntity) {
        TextRoleEntity cache = aTextRoleService.getById(textRoleEntity.getId());

        aTextRoleService.updateById(textRoleEntity);

        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getRole, textRoleEntity.getRole())
                .set(ChapterInfoEntity::getAudioModelType, textRoleEntity.getAudioModelType())
                .set(ChapterInfoEntity::getAudioModelId, textRoleEntity.getAudioModelId())
                .set(ChapterInfoEntity::getAudioConfigId, textRoleEntity.getAudioConfigId())
                .set(ChapterInfoEntity::getRefAudioId, textRoleEntity.getRefAudioId())
                .set(ChapterInfoEntity::getAudioState, ChapterInfoEntity.modified)
                .eq(ChapterInfoEntity::getProjectId, textRoleEntity.getProjectId())
                .eq(ChapterInfoEntity::getChapterId, textRoleEntity.getChapterId())
                .eq(ChapterInfoEntity::getRole, cache.getRole()));
    }

    @Override
    public void textRoleChange(TextRoleChange textRoleChange) {
        ChapterInfoEntity chapterInfoEntity = aChapterInfoService.getById(textRoleChange.getChapterInfoId());

        chapterInfoEntity.setRole(textRoleChange.getFormRoleName());
        chapterInfoEntity.setAudioState(ChapterInfoEntity.modified);

        TextRoleEntity textRoleEntity = aTextRoleService.getOne(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, textRoleChange.getProjectId())
                .eq(TextRoleEntity::getChapterId, textRoleChange.getChapterId())
                .eq(TextRoleEntity::getRole, textRoleChange.getFormRoleName()));

        if (StringUtils.equals(textRoleChange.getFromRoleType(), "role")) {

            if (Objects.nonNull(textRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioModelType(textRoleEntity.getAudioModelType());
                chapterInfoEntity.setAudioModelId(textRoleEntity.getAudioModelId());
                chapterInfoEntity.setAudioConfigId(textRoleEntity.getAudioConfigId());
                chapterInfoEntity.setRefAudioId(textRoleEntity.getRefAudioId());
            }

        }

        if (StringUtils.equals(textRoleChange.getFromRoleType(), "commonRole")) {
            TextCommonRoleEntity textCommonRoleEntity = aTextCommonRoleService.getOne(new LambdaQueryWrapper<TextCommonRoleEntity>()
                    .eq(TextCommonRoleEntity::getProjectId, textRoleChange.getProjectId())
                    .eq(TextCommonRoleEntity::getRole, textRoleChange.getFormRoleName()));

            if (Objects.nonNull(textCommonRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioModelType(textCommonRoleEntity.getAudioModelType());
                chapterInfoEntity.setAudioModelId(textCommonRoleEntity.getAudioModelId());
                chapterInfoEntity.setAudioConfigId(textCommonRoleEntity.getAudioConfigId());
                chapterInfoEntity.setRefAudioId(textCommonRoleEntity.getRefAudioId());
            }
        }

        aChapterInfoService.updateById(chapterInfoEntity);

        if (Objects.isNull(textRoleEntity)) {
            TextRoleEntity saveRole = new TextRoleEntity();
            saveRole.setProjectId(chapterInfoEntity.getProjectId());
            saveRole.setChapterId(chapterInfoEntity.getChapterId());
            saveRole.setRole(chapterInfoEntity.getRole());
            saveRole.setAudioModelType(chapterInfoEntity.getAudioModelType());
            saveRole.setAudioModelId(chapterInfoEntity.getAudioModelId());
            saveRole.setAudioConfigId(chapterInfoEntity.getAudioConfigId());
            saveRole.setRefAudioId(chapterInfoEntity.getRefAudioId());
            aTextRoleService.save(saveRole);
        }
    }

    @Override
    public void roleCombine(String projectId, String chapterId, String fromRoleName, String toRoleName) {
        List<ChapterInfoEntity> chapterInfoEntities = aChapterInfoService.list(projectId, chapterId);
        chapterInfoEntities = chapterInfoEntities.stream()
                .filter(c -> StringUtils.equals(c.getRole(), fromRoleName))
                .toList();

        TextRoleEntity toRole = aTextRoleService.getOne(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId)
                .eq(TextRoleEntity::getRole, toRoleName));


        if (!CollectionUtils.isEmpty(chapterInfoEntities) && Objects.nonNull(toRole)) {
            chapterInfoEntities = chapterInfoEntities.stream()
                    .peek(c -> {
                        c.setRole(toRole.getRole());
                        c.setAudioModelType(toRole.getAudioModelType());
                        c.setAudioModelId(toRole.getAudioModelId());
                        c.setAudioConfigId(toRole.getAudioConfigId());
                        c.setRefAudioId(toRole.getRefAudioId());
                        c.setAudioState(ChapterInfoEntity.modified);
                    }).toList();

            aChapterInfoService.saveOrUpdateBatch(chapterInfoEntities);
        }
    }

    @SneakyThrows
    @Override
    public void chapterExpose(ChapterExpose chapterExpose) {
        if (CollectionUtils.isEmpty(chapterExpose.getIndexes())) {
            return;
        }

        String projectId = chapterExpose.getProjectId();
        String chapterId = chapterExpose.getChapterId();
        Boolean combineAudio = chapterExpose.getCombineAudio();
        Boolean subtitle = chapterExpose.getSubtitle();

        TextProjectEntity textProject = aTextProjectService.getOne(projectId);
        TextChapterEntity textChapter = aTextChapterService.getOne(projectId, chapterId);

        List<ChapterInfoEntity> chapterInfos = aChapterInfoService.list(projectId, chapterId);
        chapterInfos = chapterInfos.stream()
                .filter(c -> chapterExpose.getIndexes().contains(c.getIndex()))
                .peek(c -> {
                    Path path = pathConfig.buildProjectPath(
                            "text",
                            FileUtils.fileNameFormat(textProject.getProjectName()),
                            FileUtils.fileNameFormat(textChapter.getChapterName()),
                            "audio",
                            c.getIndex() + ".wav");
                    c.setAudioPath(path.toAbsolutePath().toString());
                    c.setAudioExportFlag(true);
                })
                .toList();

        if (Objects.equals(combineAudio, Boolean.TRUE)) {
            Path outputWavPath = pathConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "output.wav");
            AudioUtils.mergeAudioFiles(chapterInfos, outputWavPath.toAbsolutePath().toString());

            Path archiveWavPath = pathConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    "output",
                    FileUtils.fileNameFormat(textChapter.getChapterName()) + ".wav");
            if (Files.notExists(archiveWavPath.getParent())) {
                Files.createDirectories(archiveWavPath.getParent());
            }
            if (Files.exists(archiveWavPath)) {
                Files.delete(archiveWavPath);
            }
            Files.copy(outputWavPath, archiveWavPath);
        }

        if (Objects.equals(subtitle, Boolean.TRUE)) {
            List<ChapterInfoEntity> handleList = chapterInfos.stream()
                    .filter(c -> Objects.equals(Boolean.TRUE, c.getAudioExportFlag())).toList();
            Path outputSrtPath = pathConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "output.srt");
            SubtitleUtils.srtFile(handleList, outputSrtPath);

            Path archiveSrtPath = pathConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    "output",
                    FileUtils.fileNameFormat(textChapter.getChapterName()) + ".srt");
            if (Files.notExists(archiveSrtPath.getParent())) {
                Files.createDirectories(archiveSrtPath.getParent());
            }
            if (Files.exists(archiveSrtPath)) {
                Files.delete(archiveSrtPath);
            }
            Files.copy(outputSrtPath, archiveSrtPath);
        }

        aChapterInfoService.updateBatchById(chapterInfos);
    }

    public void mergeAiResultInfo(String projectId, String chapterId, String aiResultStr, List<ChapterInfoEntity> chapterInfos) {

        try {

            String text = formatAiResult(aiResultStr);

            if (!JSON.isValid(text)) {
                globalWebSocketHandler.sendErrorMessage("文本大模型返回格式不正确");
            }

            AiResult aiResult = JSON.parseObject(text, AiResult.class);

            aiResult = reCombineAiResult(aiResult);

            List<AiResult.Role> roles = aiResult.getRoles();

            Map<String, AiResult.LinesMapping> linesMappingMap = aiResult.getLinesMappings()
                    .stream()
                    .collect(Collectors.toMap(AiResult.LinesMapping::getLinesIndex, Function.identity(), (a, _) -> a));

            List<TextCommonRoleEntity> commonRoles = aTextCommonRoleService.list();
            Map<String, TextCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(TextCommonRoleEntity::getRole, Function.identity(), (a, _) -> a));

            List<Integer> audioModelResetIds = new ArrayList<>();
            boolean hasAside = false;
            for (ChapterInfoEntity chapterInfo : chapterInfos) {
                String key = chapterInfo.getSecondIndex();
                String role = "旁白";
                if (linesMappingMap.containsKey(key)) {
                    AiResult.LinesMapping linesMapping = linesMappingMap.get(key);
                    role = linesMapping.getRole();
                } else {
                    hasAside = true;
                }

                chapterInfo.setRole(role);
                if (commonRoleMap.containsKey(role)) {
                    chapterInfo.setAudioModelType(commonRoleMap.get(role).getAudioModelType());
                    chapterInfo.setAudioModelId(commonRoleMap.get(role).getAudioModelId());
                    chapterInfo.setAudioConfigId(commonRoleMap.get(role).getAudioConfigId());
                    chapterInfo.setRefAudioId(commonRoleMap.get(role).getRefAudioId());
                } else {
                    audioModelResetIds.add(chapterInfo.getId());
                }
            }

            if (hasAside) {
                String role = "旁白";
                roles.add(new AiResult.Role(role));
            }

            List<TextRoleEntity> textRoleEntities = roles.stream()
                    .map(role -> {
                        TextRoleEntity textRoleEntity = new TextRoleEntity();
                        textRoleEntity.setProjectId(projectId);
                        textRoleEntity.setChapterId(chapterId);
                        textRoleEntity.setRole(role.getRole());
                        textRoleEntity.setGender(role.getGender());
                        textRoleEntity.setAgeGroup(role.getAgeGroup());

                        TextCommonRoleEntity commonRole = commonRoleMap.get(role.getRole());
                        if (Objects.nonNull(commonRole)) {
                            if (Objects.isNull(textRoleEntity.getGender())) {
                                textRoleEntity.setGender(commonRole.getGender());
                            }
                            if (Objects.isNull(textRoleEntity.getAgeGroup())) {
                                textRoleEntity.setAgeGroup(commonRole.getAgeGroup());
                            }
                            textRoleEntity.setAudioModelType(commonRoleMap.get(role.getRole()).getAudioModelType());
                            textRoleEntity.setAudioModelId(commonRoleMap.get(role.getRole()).getAudioModelId());
                            textRoleEntity.setAudioConfigId(commonRoleMap.get(role.getRole()).getAudioConfigId());
                            textRoleEntity.setRefAudioId(commonRoleMap.get(role.getRole()).getRefAudioId());
                        }

                        return textRoleEntity;
                    }).toList();


            List<RoleInferenceEntity> roleInferenceEntities = aiResult.getLinesMappings().stream()
                    .map(linesMapping -> {
                        RoleInferenceEntity roleInferenceEntity = new RoleInferenceEntity();
                        roleInferenceEntity.setProjectId(projectId);
                        roleInferenceEntity.setChapterId(chapterId);
                        roleInferenceEntity.setTextIndex(linesMapping.getLinesIndex());
                        roleInferenceEntity.setRole(linesMapping.getRole());
                        roleInferenceEntity.setGender(linesMapping.getGender());
                        roleInferenceEntity.setAgeGroup(linesMapping.getAgeGroup());
                        roleInferenceEntity.setMood(linesMapping.getMood());
                        return roleInferenceEntity;
                    }).toList();

            aChapterInfoService.updateBatchById(chapterInfos);
            aChapterInfoService.audioModelReset(audioModelResetIds);

            aTextRoleService.delete(projectId, chapterId);
            aTextRoleService.saveBatch(textRoleEntities);

            aRoleInferenceService.delete(projectId, chapterId);
            aRoleInferenceService.saveBatch(roleInferenceEntities);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatAiResult(String text) {
        if (text.startsWith("```json") || text.endsWith("```")) {
            text = text.replace("```json", "").replace("```", "");
        }
        return text;
    }

    public AiResult reCombineAiResult(AiResult aiResult) throws IOException {

        List<AiResult.Role> roles = aiResult.getRoles();
        List<AiResult.LinesMapping> linesMappings = aiResult.getLinesMappings();

        // 大模型总结的角色列表有时候会多也会少
        List<AiResult.Role> combineRoles = combineRoles(roles, linesMappings);

        AiResult result = new AiResult();
        result.setLinesMappings(linesMappings);
        result.setRoles(combineRoles);
        return result;
    }

    public List<AiResult.Role> combineRoles(List<AiResult.Role> roles, List<AiResult.LinesMapping> linesMappings) {
        Map<String, Long> linesRoleCountMap = linesMappings.stream()
                .collect(Collectors.groupingBy(AiResult.LinesMapping::getRole, Collectors.counting()));
        List<AiResult.Role> filterRoles = roles.stream().filter(r -> linesRoleCountMap.containsKey(r.getRole())).toList();

        Set<String> filterRoleSet = filterRoles.stream().map(AiResult.Role::getRole).collect(Collectors.toSet());
        List<AiResult.Role> newRoleList = linesMappings.stream().filter(m -> !filterRoleSet.contains(m.getRole()))
                .map(m -> {
                    AiResult.Role role = new AiResult.Role();
                    role.setRole(m.getRole());
                    role.setGender(m.getGender());
                    role.setAgeGroup(m.getAgeGroup());
                    return role;
                })
                .collect(Collectors.toMap(AiResult.Role::getRole, Function.identity(), (v1, _) -> v1))
                .values().stream().toList();

        List<AiResult.Role> newRoles = new ArrayList<>();
        newRoles.addAll(filterRoles);
        newRoles.addAll(newRoleList);
        newRoles.sort(Comparator.comparingLong((AiResult.Role r) -> linesRoleCountMap.get(r.getRole())).reversed());
        return newRoles;
    }


    static String parseStep = """
            1. 分析下面原文中有哪些角色，角色中有观众、群众之类的角色时统一使用观众这个角色，他们的性别和年龄段只能在下面范围中选择一个：
            性别：男、女、未知。
            年龄段：少年、青年、中年、老年、未知。

            2. 请分析下面台词部分的内容是属于原文部分中哪个角色的，然后结合上下文分析当时的情绪，情绪只能在下面范围中选择一个：
            情绪：中立、开心、吃惊、难过、厌恶、生气、恐惧。

            3. 严格按照台词文本中的顺序在原文文本中查找。每行台词都做一次处理，不能合并台词。
            4. 返回结果是JSON数组结构的字符串。
            5. 分析的台词内容如果不是台词，不要加入到返回结果中。
            """;

    static String outputFormat = """
            {
              "roles": [
                {
                  "role": "这里是具体的角色名",
                  "gender": "男",
                  "ageGroup": "青年"
                }
              ],
              "linesMappings": [
                {
                  "linesIndex": "这里的值是台词前的序号",
                  "role": "这里是具体的角色名",
                  "gender": "男",
                  "ageGroup": "青年",
                  "mood": "自卑"
                }
              ]
            }
            """;
}

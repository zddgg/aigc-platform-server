package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.service.application.*;
import space.wenliang.ai.aigcplatformserver.service.business.BChapterInfoService;
import space.wenliang.ai.aigcplatformserver.socket.AudioProcessWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BChapterInfoServiceImpl implements BChapterInfoService {

    public static final LinkedBlockingDeque<ChapterInfoEntity> audioCreateTaskQueue = new LinkedBlockingDeque<>();

    private final AChapterInfoService aChapterInfoService;
    private final ATextChapterService aTextChapterService;
    private final ATextProjectService aTextProjectService;

    private final ARefAudioService aRefAudioService;
    private final AGptSovitsModelService aGptSovitsModelService;
    private final AGptSovitsConfigService aGptSovitsConfigService;
    private final AFishSpeechModelService aFishSpeechModelService;
    private final AFishSpeechConfigService aFishSpeechConfigService;
    private final AChatTtsConfigService aChatTtsConfigService;
    private final AEdgeTtsConfigService aEdgeTtsConfigService;

    private final ATextCommonRoleService aTextCommonRoleService;
    private final ATextRoleService aTextRoleService;
    private final AudioCreator audioCreator;
    private final PathConfig pathConfig;

    private final AudioProcessWebSocketHandler audioProcessWebSocketHandler;

    public BChapterInfoServiceImpl(AChapterInfoService aChapterInfoService,
                                   ATextChapterService aTextChapterService, ATextProjectService aTextProjectService,
                                   ARefAudioService aRefAudioService,
                                   AGptSovitsModelService aGptSovitsModelService,
                                   AGptSovitsConfigService aGptSovitsConfigService,
                                   AFishSpeechModelService aFishSpeechModelService,
                                   AFishSpeechConfigService aFishSpeechConfigService,
                                   AChatTtsConfigService aChatTtsConfigService,
                                   AEdgeTtsConfigService aEdgeTtsConfigService,
                                   ATextCommonRoleService aTextCommonRoleService,
                                   ATextRoleService aTextRoleService,
                                   AudioCreator audioCreator,
                                   PathConfig pathConfig,
                                   AudioProcessWebSocketHandler audioProcessWebSocketHandler) {
        this.aChapterInfoService = aChapterInfoService;
        this.aTextChapterService = aTextChapterService;
        this.aTextProjectService = aTextProjectService;
        this.aRefAudioService = aRefAudioService;
        this.aGptSovitsModelService = aGptSovitsModelService;
        this.aGptSovitsConfigService = aGptSovitsConfigService;
        this.aFishSpeechModelService = aFishSpeechModelService;
        this.aFishSpeechConfigService = aFishSpeechConfigService;
        this.aChatTtsConfigService = aChatTtsConfigService;
        this.aEdgeTtsConfigService = aEdgeTtsConfigService;
        this.aTextCommonRoleService = aTextCommonRoleService;
        this.aTextRoleService = aTextRoleService;
        this.audioCreator = audioCreator;
        this.pathConfig = pathConfig;
        this.audioProcessWebSocketHandler = audioProcessWebSocketHandler;
    }

    @PostConstruct
    public void init() {
        audioCreateTask();
    }

    @Override
    public List<ChapterInfoEntity> chapterInfos(String projectId, String chapterId) {

        TextProjectEntity textProject = aTextProjectService.getOne(projectId);
        TextChapterEntity textChapter = aTextChapterService.getOne(projectId, chapterId);

        List<ChapterInfoEntity> chapterInfos = aChapterInfoService.list(projectId, chapterId);

        if (CollectionUtils.isEmpty(chapterInfos)) {
            TextChapterEntity textChapterEntity = aTextChapterService.getOne(projectId, chapterId);

            List<ChapterInfoEntity> chapterInfoEntities = initChapterInfo(textChapterEntity);

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

                aChapterInfoService.saveBatch(chapterInfoEntities);
                aTextRoleService.save(textRoleEntity);

            } else {
                return new ArrayList<>();
            }

            chapterInfos = chapterInfoEntities;
        }

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

        return chapterInfos
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
                    if (Objects.equals(e.getAudioState(), ChapterInfoEntity.created)) {
                        String audioUrl = pathConfig.buildProjectUrl(
                                "text",
                                FileUtils.fileNameFormat(textProject.getProjectName()),
                                FileUtils.fileNameFormat(textChapter.getChapterName()),
                                "audio",
                                e.getIndex() + ".wav");
                        e.setAudioUrl(audioUrl);
                    }

                })
                .toList();
    }

    @Override
    public void audioModelChange(ChapterInfoEntity chapterInfoEntity) {
        aChapterInfoService.updateById(chapterInfoEntity);
    }

    @Override
    public void updateVolume(ChapterInfoEntity chapterInfoEntity) {
        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioVolume, chapterInfoEntity.getAudioVolume())
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateSpeed(ChapterInfoEntity chapterInfoEntity) {
        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioSpeed, chapterInfoEntity.getAudioSpeed())
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateInterval(ChapterInfoEntity chapterInfoEntity) {
        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getNextAudioInterval, chapterInfoEntity.getNextAudioInterval())
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateControls(ControlsUpdate controlsUpdate) {
        List<ChapterInfoEntity> chapterInfoEntities = aChapterInfoService.list(controlsUpdate.getProjectId(), controlsUpdate.getChapterId());
        for (ChapterInfoEntity chapterInfoEntity : chapterInfoEntities) {
            if (Objects.equals(controlsUpdate.getEnableVolume(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioVolume(controlsUpdate.getVolume());
            }
            if (Objects.equals(controlsUpdate.getEnableSpeed(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioSpeed(controlsUpdate.getSpeed());
            }
            if (Objects.equals(controlsUpdate.getEnableInterval(), Boolean.TRUE)) {
                chapterInfoEntity.setNextAudioInterval(controlsUpdate.getInterval());
            }
        }

        aChapterInfoService.updateBatchById(chapterInfoEntities);
    }

    @Override
    public void updateChapterText(ChapterInfoEntity chapterInfoEntity) {
        aChapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getText, chapterInfoEntity.getText())
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public List<String> addAudioCreateTask(ChapterInfoEntity chapterInfoEntity) {
        audioCreateTaskQueue.add(chapterInfoEntity);
        List<String> creatingIds = new ArrayList<>();
        audioCreateTaskQueue.forEach(t -> creatingIds.add(t.getIndex()));
        return creatingIds;
    }

    @Override
    public Tuple2<Integer, List<String>> startCreateAudio(String projectId, String chapterId, String actionType) {
        List<ChapterInfoEntity> entities = aChapterInfoService.list(projectId, chapterId)
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getAudioModelType()))
                .filter(c -> {
                    if (StringUtils.equals(actionType, "all")) {
                        return true;
                    }
                    return !Objects.equals(c.getAudioState(), ChapterInfoEntity.created);
                })
                .toList();
        audioCreateTaskQueue.addAll(entities);

        List<String> creatingIds = new ArrayList<>();
        entities.forEach(t -> creatingIds.add(t.getIndex()));

        return Tuple.of(entities.size(), creatingIds);
    }

    @Override
    public void stopCreateAudio() {
        audioCreateTaskQueue.clear();
    }

    public void audioCreateTask() {
        CompletableFuture.runAsync(() -> {
                    while (true) {
                        try {
                            ChapterInfoEntity audioContext = audioCreateTaskQueue.takeFirst();
                            createAudio(audioContext);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }, Executors.newFixedThreadPool(1))
                .exceptionally(e -> {
                    log.error(e.getMessage(), e);
                    return null;
                });
    }

    private void createAudio(ChapterInfoEntity chapterInfo) throws Exception {
        AudioContext audioContext = new AudioContext();
        audioContext.setType(chapterInfo.getAudioModelType());
        audioContext.setModelId(chapterInfo.getAudioModelId());
        audioContext.setConfigId(chapterInfo.getAudioConfigId());
        audioContext.setRefAudioId(chapterInfo.getRefAudioId());
        audioContext.setText(chapterInfo.getText());

        TextProjectEntity textProject = aTextProjectService.getOne(chapterInfo.getProjectId());
        TextChapterEntity textChapter = aTextChapterService.getOne(chapterInfo.getProjectId(), chapterInfo.getChapterId());

        Path outputDir = pathConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "audio");

        audioContext.setOutputDir(outputDir.toAbsolutePath().toString());
        audioContext.setOutputName(chapterInfo.getIndex());

        audioCreator.createFile(audioContext);

        aChapterInfoService.updateAudioStage(chapterInfo.getId(), ChapterInfoEntity.created);

        chapterInfo.setAudioUrl(pathConfig.buildProjectUrl(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "audio",
                chapterInfo.getIndex() + ".wav"));

        JSONObject j1 = new JSONObject();
        j1.put("type", "result");
        j1.put("projectId", chapterInfo.getProjectId());
        j1.put("chapterId", chapterInfo.getChapterId());
        j1.put("chapterInfo", chapterInfo);
        audioProcessWebSocketHandler.sendMessageToProject(chapterInfo.getProjectId(), JSON.toJSONString(j1));

        JSONObject j2 = new JSONObject();
        j2.put("type", "stage");
        j1.put("projectId", chapterInfo.getProjectId());
        j1.put("chapterId", chapterInfo.getChapterId());
        j2.put("taskNum", audioCreateTaskQueue.size());

        List<String> creatingIds = new ArrayList<>();
        audioCreateTaskQueue.forEach(t -> creatingIds.add(t.getIndex()));
        j2.put("creatingIds", creatingIds);
        audioProcessWebSocketHandler.sendMessageToProject(chapterInfo.getProjectId(), JSON.toJSONString(j2));
    }


    private List<ChapterInfoEntity> initChapterInfo(TextChapterEntity textChapterEntity) {
        if (Objects.isNull(textChapterEntity) || StringUtils.isBlank(textChapterEntity.getContent())) {
            return new ArrayList<>();
        }

        List<String> dialoguePatterns = StringUtils.isBlank(textChapterEntity.getDialoguePattern())
                ? List.of()
                : List.of(textChapterEntity.getDialoguePattern());

        List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

        int paragraphIndex = 0;

        for (String line : textChapterEntity.getContent().split("\n")) {
            List<Tuple2<String, Boolean>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);
            int sentenceIndex = 0;

            for (Tuple2<String, Boolean> chapterInfoTuple2 : chapterInfoTuple2s) {
                ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                chapterInfoEntity.setProjectId(textChapterEntity.getProjectId());
                chapterInfoEntity.setChapterId(textChapterEntity.getChapterId());
                chapterInfoEntity.setParagraphIndex(paragraphIndex);
                chapterInfoEntity.setSentenceIndex(sentenceIndex);
                chapterInfoEntity.setText(chapterInfoTuple2._1);
                chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._2);

                chapterInfoEntity.setRole("旁白");

                chapterInfoEntities.add(chapterInfoEntity);

                sentenceIndex++;
            }

            paragraphIndex++;
        }

        return chapterInfoEntities;
    }
}
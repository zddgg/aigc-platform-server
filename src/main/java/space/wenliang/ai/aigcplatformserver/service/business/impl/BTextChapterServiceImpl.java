package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import space.wenliang.ai.aigcplatformserver.ai.chat.AiService;
import space.wenliang.ai.aigcplatformserver.bean.*;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.common.Page;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.*;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BTextChapterServiceImpl implements BTextChapterService {

    static String parseStep = """
            1. 分析下面原文中有哪些角色，角色中有观众、群众之类的角色时统一使用观众这个角色，他们的性别和年龄段只能在下面范围中选择一个：
            性别：男、女、未知。
            年龄段：少年、青年、中年、老年、未知。
            
            2. 请分析下面台词部分的内容是属于原文部分中哪个角色的，然后结合上下文分析当时的情绪，情绪只能在下面范围中选择一个：
            情绪：中立、开心、吃惊、难过、厌恶、生气、恐惧。
            
            3. 严格按照台词文本中的顺序在原文文本中查找。每行台词都做一次处理，不能合并台词。
            4. 分析的台词内容如果不是台词，不要加入到返回结果中。
            """;
    static String outputFormat = """
            roles:
            角色名,男,青年
            角色名,男,青年
            
            linesMappings:
            台词序号,角色名,高兴
            台词序号,角色名,难过
            """;
    static String temp = """
            roles:
            萧炎,男,青年
            中年男子,男,中年
            少女,女,少年
            萧媚,女,少年
            萧薰儿,女,少年
            观众,未知,未知
            
            linesMappings:
            1-0,萧炎,难过
            3-0,中年男子,中立
            5-0,观众,厌恶
            6-0,观众,生气
            7-0,观众,厌恶
            8-0,观众,难过
            9-0,观众,中立
            12-0,萧炎,难过
            13-0,中年男子,中立
            18-0,中年男子,中立
            19-0,中年男子,中立
            20-0,萧媚,开心
            21-0,观众,中立
            22-0,观众,中立
            26-0,萧炎,难过
            32-0,中年男子,中立
            40-0,中年男子,中立
            42-0,观众,吃惊
            45-1,中年男子,中立
            47-0,萧薰儿,中立
            48-0,萧薰儿,中立
            49-0,萧炎,难过
            50-0,萧薰儿,中立
            51-0,萧炎,难过
            52-1,萧薰儿,中立
            52-3,萧薰儿,中立
            53-0,萧炎,尴尬
            """;
    private final EnvConfig envConfig;
    private final AiService aiService;
    private final TextRoleService textRoleService;
    private final TextChapterService textChapterService;
    private final ChapterInfoService chapterInfoService;
    private final TextProjectService textProjectService;
    private final TextCommonRoleService textCommonRoleService;
    private final TextRoleInferenceService textRoleInferenceService;
    private final AmModelFileService amModelFileService;
    private final AmModelConfigService amModelConfigService;
    private final AmPromptAudioService amPromptAudioService;
    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final GlobalSettingService globalSettingService;

    @Override
    public Page<TextChapterEntity> pageChapters(ProjectQuery projectQuery) {
        Page<TextChapterEntity> page = textChapterService.page(
                Page.of(projectQuery.getCurrent(), projectQuery.getPageSize()),
                new LambdaQueryWrapper<TextChapterEntity>()
                        .eq(TextChapterEntity::getProjectId, projectQuery.getProjectId())
                        .orderByAsc(TextChapterEntity::getSortOrder, TextChapterEntity::getId));

        if (!CollectionUtils.isEmpty(page.getRecords())) {

            Map<String, Integer> chapterTextCount = chapterInfoService.chapterGroupCount();
            Map<String, Integer> chapterExportCount = chapterInfoService.chapterExportCount();
            Map<String, Integer> chapterRoleCount = textRoleService.chapterGroupCount();

            List<TextChapterEntity> list = page.getRecords().stream()
                    .peek(t -> {
                        t.setTextNum(chapterTextCount.get(t.getChapterId()));
                        t.setRoleNum(chapterRoleCount.get(t.getChapterId()));
                        t.setAudioTaskState(chapterExportCount.get(t.getChapterId()));
                    }).toList();

            page.setRecords(list);
        }
        return page;
    }

    @Override
    public List<TextChapterEntity> chapters4Sort(String projectId) {
        return textChapterService.list(new LambdaQueryWrapper<TextChapterEntity>()
                        .select(TextChapterEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                        .eq(TextChapterEntity::getProjectId, projectId)
                        .orderByAsc(TextChapterEntity::getSortOrder, TextChapterEntity::getId))
                .stream()
                .toList();
    }

    @Override
    public void deleteChapter(TextChapterEntity textChapter) throws IOException {
        TextProjectEntity project = textProjectService.getByProjectId(textChapter.getProjectId());

        chapterInfoService.deleteByChapterId(textChapter.getChapterId());
        textChapterService.deleteByChapterId(textChapter.getChapterId());

        textRoleService.deleteByChapterId(textChapter.getChapterId());
        textRoleInferenceService.deleteByChapterId(textChapter.getChapterId());

        FileUtils.deleteDirectoryAll(Path.of(
                envConfig.getProjectDir(),
                "text",
                FileUtils.fileNameFormat(project.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName())
        ));
    }

    @Override
    public List<ChapterInfoEntity> tmpDialogueParse(TextChapterEntity textChapter) {
        List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

        if (StringUtils.isNotBlank(textChapter.getContent())) {
            List<String> dialoguePatterns = StringUtils.isBlank(textChapter.getDialoguePattern())
                    ? List.of()
                    : List.of(textChapter.getDialoguePattern());

            for (String line : textChapter.getContent().split("\n")) {
                List<Tuple2<Boolean, String>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

                for (Tuple2<Boolean, String> chapterInfoTuple2 : chapterInfoTuple2s) {

                    ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                    chapterInfoEntity.setText(chapterInfoTuple2._2);
                    chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._1);
                    chapterInfoEntities.add(chapterInfoEntity);
                }
            }
        }

        return chapterInfoEntities;
    }

    @Override
    public void chapterEdit(TextChapterEntity textChapter) {
        String chapterId = textChapter.getChapterId();

        TextChapterEntity entity = textChapterService.getByChapterId(chapterId);

        if (Objects.nonNull(entity) && StringUtils.isNotBlank(textChapter.getContent())) {

            saveChapterInfoEntities(textChapter);

            entity.setChapterName(textChapter.getChapterName());
            entity.setContent(textChapter.getContent());
            entity.setDialoguePattern(textChapter.getDialoguePattern());
            textChapterService.updateById(entity);
        }
    }

    @Override
    public void chapterAdd(ChapterAdd chapterAdd) {
        TextChapterEntity textChapter = chapterAdd.getTextChapter();
        List<TextChapterEntity> sortChapters = chapterAdd.getSortChapters();

        String projectId = textChapter.getProjectId();
        String chapterId = IdUtils.uuid();

        textChapter.setChapterId(chapterId);

        if (StringUtils.isNotBlank(textChapter.getContent())) {

            saveChapterInfoEntities(textChapter);


            TextChapterEntity save = new TextChapterEntity();
            save.setProjectId(projectId);
            save.setChapterId(chapterId);
            save.setChapterName(textChapter.getChapterName());
            save.setContent(textChapter.getContent());
            save.setDialoguePattern(textChapter.getDialoguePattern());
            save.setSortOrder(Optional.ofNullable(textChapter.getSortOrder()).orElse(0));

            textChapterService.save(save);

            if (!CollectionUtils.isEmpty(sortChapters)) {
                List<TextChapterEntity> saveList = sortChapters.stream()
                        .map(t -> {
                            TextChapterEntity textChapterEntity = new TextChapterEntity();
                            textChapterEntity.setId(t.getId());
                            textChapterEntity.setSortOrder(t.getSortOrder());
                            return textChapterEntity;
                        }).toList();
                textChapterService.updateBatchById(saveList);
            }
        }
    }

    @Override
    public void chapterSort(List<TextChapterEntity> sortChapters) {
        if (!CollectionUtils.isEmpty(sortChapters)) {
            List<TextChapterEntity> saveList = sortChapters.stream().map(t -> {
                TextChapterEntity textChapterEntity = new TextChapterEntity();
                textChapterEntity.setId(t.getId());
                textChapterEntity.setSortOrder(t.getSortOrder());
                return textChapterEntity;
            }).toList();
            textChapterService.updateBatchById(saveList);
        }
    }

    @Override
    public List<TextRoleEntity> roles(String chapterId) {
        List<TextRoleEntity> roleEntities = textRoleService.getByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(roleEntities)) {
            Map<String, Long> roleCountMap = chapterInfoService.getByChapterId(chapterId)
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
                CompletableFuture.runAsync(() -> textRoleService
                        .removeByIds(deleteList.stream().map(TextRoleEntity::getId).toList()));
            }
        }

        return roleEntities;
    }

    @Override
    public void updateRole(TextRoleEntity textRoleEntity) {
        TextRoleEntity cache = textRoleService.getById(textRoleEntity.getId());

        textRoleService.update(new LambdaUpdateWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getId, textRoleEntity.getId())
                .set(TextRoleEntity::getRole, textRoleEntity.getRole())
                .set(TextRoleEntity::getGender, textRoleEntity.getGender())
                .set(TextRoleEntity::getAge, textRoleEntity.getAge()));

        chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getRole, textRoleEntity.getRole())
                .set(ChapterInfoEntity::getGender, textRoleEntity.getGender())
                .set(ChapterInfoEntity::getAge, textRoleEntity.getAge())
                .eq(ChapterInfoEntity::getProjectId, textRoleEntity.getProjectId())
                .eq(ChapterInfoEntity::getChapterId, textRoleEntity.getChapterId())
                .eq(ChapterInfoEntity::getRole, cache.getRole()));
    }

    @Override
    public void updateRoleModel(UpdateModelInfo updateModelInfo) {

        for (Integer id : updateModelInfo.getIds()) {
            TextRoleEntity cache = textRoleService.getById(id);

            AmModelFileEntity modelFile = amModelFileService.getByMfId(updateModelInfo.getAmMfId());
            AmModelConfigEntity modelConfig = amModelConfigService.getByMcId(updateModelInfo.getAmMcId());
            AmPromptAudioEntity promptAudio = amPromptAudioService.getByPaId(updateModelInfo.getAmPaId());

            List<ChapterInfoEntity> updateList = chapterInfoService.list(new LambdaQueryWrapper<ChapterInfoEntity>()
                    .eq(ChapterInfoEntity::getProjectId, updateModelInfo.getProjectId())
                    .eq(ChapterInfoEntity::getChapterId, updateModelInfo.getChapterId())
                    .eq(ChapterInfoEntity::getRole, cache.getRole()));

            for (ChapterInfoEntity chapterInfo : updateList) {
                chapterInfo.setAudioTaskState(AudioTaskStateConstants.modified);
                chapterInfo.setAmType(updateModelInfo.getAmType());
                chapterInfo.setModelFile(modelFile);
                chapterInfo.setModelConfig(modelConfig);
                chapterInfo.setPromptAudio(promptAudio);

                if (StringUtils.isNotBlank(updateModelInfo.getAmMcParamsJson())) {
                    chapterInfo.setAmMcParamsJson(updateModelInfo.getAmMcParamsJson());
                }
            }

            chapterInfoService.updateBatchById(updateList);

            TextRoleEntity update = new TextRoleEntity();
            update.setId(id);
            update.setAmType(updateModelInfo.getAmType());
            update.setModelFile(modelFile);
            update.setModelConfig(modelConfig);
            update.setPromptAudio(promptAudio);

            if (StringUtils.isNotBlank(updateModelInfo.getAmMcParamsJson())) {
                update.setAmMcParamsJson(updateModelInfo.getAmMcParamsJson());
            }

            textRoleService.updateById(update);
        }
    }

    @Override
    public void roleCombine(String projectId, String chapterId, String fromRoleName, String toRoleName) {
        List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.getByChapterId(chapterId)
                .stream()
                .filter(c -> StringUtils.equals(c.getRole(), fromRoleName))
                .toList();

        TextRoleEntity toRole = textRoleService.getOne(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId)
                .eq(TextRoleEntity::getRole, toRoleName));


        if (!CollectionUtils.isEmpty(chapterInfoEntities) && Objects.nonNull(toRole)) {
            chapterInfoEntities = chapterInfoEntities.stream()
                    .peek(c -> {
                        c.setAudioRoleInfo(toRole);
                        c.setAudioModelInfo(toRole);
                    }).toList();

            chapterInfoService.saveOrUpdateBatch(chapterInfoEntities);
        }
    }

    @Override
    public void textRoleChange(TextRoleChange textRoleChange) {
        List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.listByIds(textRoleChange.getChapterInfoIds());

        TextRoleEntity textRoleEntity = textRoleService.getOne(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, textRoleChange.getProjectId())
                .eq(TextRoleEntity::getChapterId, textRoleChange.getChapterId())
                .eq(TextRoleEntity::getRole, textRoleChange.getFormRoleName()));

        for (ChapterInfoEntity chapterInfoEntity : chapterInfoEntities) {
            chapterInfoEntity.setRole(textRoleChange.getFormRoleName());
            chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.modified);


            if (StringUtils.equals(textRoleChange.getFromRoleType(), "role")) {

                if (Objects.nonNull(textRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                    chapterInfoEntity.setAudioRoleInfo(textRoleEntity);
                    chapterInfoEntity.setAudioModelInfo(textRoleEntity);
                }

            }

            if (StringUtils.equals(textRoleChange.getFromRoleType(), "commonRole")) {
                TextCommonRoleEntity textCommonRoleEntity = textCommonRoleService.getOne(
                        new LambdaQueryWrapper<TextCommonRoleEntity>()
                                .eq(TextCommonRoleEntity::getProjectId, textRoleChange.getProjectId())
                                .eq(TextCommonRoleEntity::getRole, textRoleChange.getFormRoleName()));

                if (Objects.nonNull(textCommonRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                    chapterInfoEntity.setAudioRoleInfo(textCommonRoleEntity);
                    chapterInfoEntity.setAudioModelInfo(textCommonRoleEntity);
                }
            }
        }

        chapterInfoService.updateBatchById(chapterInfoEntities);

        if (Objects.isNull(textRoleEntity)) {
            TextRoleEntity saveRole = new TextRoleEntity();
            saveRole.setProjectId(textRoleChange.getProjectId());
            saveRole.setChapterId(textRoleChange.getChapterId());
            saveRole.setRole(textRoleChange.getFormRoleName());
            saveRole.setAudioModelInfo(chapterInfoEntities.getFirst());
            textRoleService.save(saveRole);
        }
    }

    @Override
    public Boolean saveToCommonRole(TextRoleEntity textRoleEntity) {
        List<TextCommonRoleEntity> commonRoleEntities = textCommonRoleService.list(
                new LambdaQueryWrapper<TextCommonRoleEntity>()
                        .eq(TextCommonRoleEntity::getRole, textRoleEntity.getRole()));

        if (!Objects.equals(textRoleEntity.getCoverCommonRole(), Boolean.TRUE)
                && !CollectionUtils.isEmpty(commonRoleEntities)) {
            return false;
        }

        if (!CollectionUtils.isEmpty(commonRoleEntities)) {
            textCommonRoleService.removeByIds(commonRoleEntities.stream().map(TextCommonRoleEntity::getId).toList());
        }

        TextCommonRoleEntity textCommonRoleEntity = new TextCommonRoleEntity();
        textCommonRoleEntity.setProjectId(textRoleEntity.getProjectId());
        textCommonRoleEntity.setAudioRoleInfo(textRoleEntity);
        textCommonRoleEntity.setAudioModelInfo(textRoleEntity);
        textCommonRoleService.save(textCommonRoleEntity);

        return true;
    }

    @Override
    public List<TextCommonRoleEntity> commonRoles(String projectId) {
        return textCommonRoleService.getByProjectId(projectId);
    }

    @Override
    public void createCommonRole(TextCommonRoleEntity textCommonRoleEntity) {
        String amMcParamsJson = textCommonRoleEntity.getAmMcParamsJson();

        textCommonRoleService.getByProjectId(textCommonRoleEntity.getProjectId())
                .stream()
                .filter(r -> StringUtils.equals(r.getRole(), textCommonRoleEntity.getRole()))
                .findAny()
                .ifPresent(r -> {
                    throw new BizException("预置角色名称[" + r.getRole() + "]已存在");
                });

        textCommonRoleEntity.setModelFile(amModelFileService.getByMfId(textCommonRoleEntity.getAmMfId()));
        textCommonRoleEntity.setModelConfig(amModelConfigService.getByMcId(textCommonRoleEntity.getAmMcId()));
        textCommonRoleEntity.setPromptAudio(amPromptAudioService.getByPaId(textCommonRoleEntity.getAmPaId()));

        if (StringUtils.isNotBlank(amMcParamsJson)) {
            textCommonRoleEntity.setAmMcParamsJson(amMcParamsJson);
        }

        textCommonRoleService.save(textCommonRoleEntity);
    }

    @Override
    public void updateCommonRole(UpdateModelInfo updateModelInfo) {

        List<TextCommonRoleEntity> updateList = updateModelInfo.getIds()
                .stream()
                .map(id -> {
                    TextCommonRoleEntity update = new TextCommonRoleEntity();

                    update.setId(id);
                    update.setRole(updateModelInfo.getRole());
                    update.setGender(updateModelInfo.getGender());
                    update.setAge(updateModelInfo.getAge());
                    update.setAmType(updateModelInfo.getAmType());
                    update.setModelFile(amModelFileService.getByMfId(updateModelInfo.getAmMfId()));
                    update.setModelConfig(amModelConfigService.getByMcId(updateModelInfo.getAmMcId()));
                    update.setPromptAudio(amPromptAudioService.getByPaId(updateModelInfo.getAmPaId()));

                    if (StringUtils.isNotBlank(updateModelInfo.getAmMcParamsJson())) {
                        update.setAmMcParamsJson(updateModelInfo.getAmMcParamsJson());
                    }

                    return update;
                }).toList();

        textCommonRoleService.updateBatchById(updateList);
    }

    @Override
    public void deleteCommonRole(TextCommonRoleEntity textCommonRoleEntity) {
        textCommonRoleService.removeById(textCommonRoleEntity);
    }

    @Override
    public Object checkRoleInference(String projectId, String chapterId) {
        List<TextRoleInferenceEntity> list = textRoleInferenceService.getByChapterId(chapterId);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public void loadRoleInference(String projectId, String chapterId) {
        List<TextRoleInferenceEntity> roleInferenceEntities = textRoleInferenceService.getByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(roleInferenceEntities)) {
            List<TextCommonRoleEntity> commonRoles = textCommonRoleService.list();
            Map<String, TextCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(TextCommonRoleEntity::getRole, Function.identity(), (a, _) -> a));

            List<TextRoleEntity> textRoleEntities = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(TextRoleInferenceEntity::getRole, Function.identity(), (v1, _) -> v1))
                    .values()
                    .stream().map(roleInferenceEntity -> {
                        TextRoleEntity textRoleEntity = new TextRoleEntity();
                        textRoleEntity.setProjectId(projectId);
                        textRoleEntity.setChapterId(chapterId);
                        textRoleEntity.setRole(roleInferenceEntity.getRole());
                        textRoleEntity.setGender(roleInferenceEntity.getGender());
                        textRoleEntity.setAge(roleInferenceEntity.getAge());
                        TextCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());
                        if (Objects.nonNull(commonRole)) {
                            if (Objects.isNull(textRoleEntity.getGender())) {
                                textRoleEntity.setGender(commonRole.getGender());
                            }
                            if (Objects.isNull(textRoleEntity.getAge())) {
                                textRoleEntity.setAge(commonRole.getAge());
                            }
                            textRoleEntity.setAudioModelInfo(commonRole);
                        }
                        return textRoleEntity;
                    }).toList();

            Map<String, TextRoleInferenceEntity> roleInferenceEntityMap = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(TextRoleInferenceEntity::getTextIndex, Function.identity(), (a, _) -> a));

            List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.getByChapterId(chapterId);

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
                    .filter(c -> roleInferenceEntityMap.containsKey(c.getIndex()))
                    .peek(c -> {
                        TextRoleInferenceEntity roleInferenceEntity = roleInferenceEntityMap.get(c.getIndex());
                        c.setRole(roleInferenceEntity.getRole());

                        if (commonRoleMap.containsKey(roleInferenceEntity.getRole())) {
                            TextCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());

                            c.setAudioModelInfo(commonRole);

                            c.setRole(roleInferenceEntity.getRole());
                            c.setGender(roleInferenceEntity.getGender());
                            c.setAge(roleInferenceEntity.getAge());
                            if (Objects.nonNull(commonRole.getGender())) {
                                c.setGender(commonRole.getGender());
                            }
                            if (Objects.nonNull(commonRole.getAge())) {
                                c.setAge(commonRole.getAge());
                            }
                        } else {
                            audioModelResetIds.add(c.getId());
                        }
                    }).toList();

            chapterInfoService.updateBatchById(saveInfos);
            chapterInfoService.audioModelReset(audioModelResetIds);

            ArrayList<TextRoleEntity> saveTextRoles = new ArrayList<>(textRoleEntities);

            if (hasAsideRole.isEmpty() && hasAsideText.isPresent()) {

                TextRoleEntity textRoleEntity = new TextRoleEntity();
                textRoleEntity.setProjectId(projectId);
                textRoleEntity.setChapterId(chapterId);
                textRoleEntity.setRole(asideRole);
                TextCommonRoleEntity commonRole = commonRoleMap.get(asideRole);
                if (Objects.nonNull(commonRole)) {
                    textRoleEntity.setAudioModelInfo(commonRole);
                }
                saveTextRoles.add(textRoleEntity);
            }

            textRoleService.deleteByChapterId(chapterId);
            textRoleService.saveBatch(saveTextRoles);
        }
    }

    @Override
    public void chapterExpose(ChapterExpose chapterExpose) throws Exception {
        if (CollectionUtils.isEmpty(chapterExpose.getChapterInfoIds())) {
            return;
        }

        String projectId = chapterExpose.getProjectId();
        String chapterId = chapterExpose.getChapterId();
        Boolean combineAudio = chapterExpose.getCombineAudio();
        Boolean subtitle = chapterExpose.getSubtitle();

        TextProjectEntity textProject = textProjectService.getByProjectId(projectId);
        TextChapterEntity textChapter = textChapterService.getByChapterId(chapterId);

        List<ChapterInfoEntity> chapterInfos = chapterInfoService.getByChapterId(chapterId);

        Boolean subtitleOptimize = globalSettingService.getGlobalSetting().getSubtitleOptimize();

        List<AudioSegment> audioSegments = chapterInfos.stream()
                .filter(c -> chapterExpose.getChapterInfoIds().contains(c.getId()))
                .sorted(Comparator.comparingInt((ChapterInfoEntity entity) -> Optional.ofNullable(entity.getTextSort()).orElse(0))
                        .thenComparingInt(ChapterInfoEntity::getId))
                .map(c -> {

                    List<String> subtitles = SubtitleUtils.subtitleSplit(c.getText(), subtitleOptimize);

                    String[] audioNames = c.getAudioFiles().split(",");

                    if (CollectionUtils.isEmpty(subtitles) || subtitles.size() != audioNames.length) {
                        return null;
                    }

                    List<AudioSegment> subAudioSegments = new ArrayList<>();

                    for (int i = 0; i < audioNames.length; i++) {

                        AudioSegment subAudioSegment = new AudioSegment();
                        subAudioSegment.setId(c.getId());
                        subAudioSegment.setPart(i);
                        subAudioSegment.setAudioName(audioNames[i]);
                        subAudioSegment.setText(subtitles.get(i));
                        subAudioSegment.setAudioVolume(c.getAudioVolume());
                        subAudioSegment.setAudioSpeed(c.getAudioSpeed());

                        if (i == audioNames.length - 1) {
                            subAudioSegment.setAudioInterval(c.getAudioInterval());
                        } else {
                            subAudioSegment.setAudioInterval(globalSettingService.getGlobalSetting().getSubAudioInterval());
                        }

                        Path subPath = envConfig.buildProjectPath(
                                "text",
                                FileUtils.fileNameFormat(textProject.getProjectName()),
                                FileUtils.fileNameFormat(textChapter.getChapterName()),
                                "audio",
                                audioNames[i]);

                        subAudioSegment.setAudioPath(subPath.toAbsolutePath().toString());

                        subAudioSegments.add(subAudioSegment);
                    }

                    return subAudioSegments;
                }).filter(Objects::nonNull).flatMap(Collection::stream).toList();

        if (Objects.equals(combineAudio, Boolean.TRUE)) {
            Path outputWavPath = envConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "output.wav");

            if (Files.notExists(outputWavPath.getParent())) {
                Files.createDirectories(outputWavPath.getParent());
            }

            AudioUtils.mergeAudioFiles(audioSegments, outputWavPath.toAbsolutePath().toString());

            List<ChapterInfoEntity> updateList = audioSegments.stream()
                    .map(a -> {
                        ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                        chapterInfoEntity.setId(a.getId());
                        chapterInfoEntity.setAudioLength(a.getAudioLength());
                        chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.combined);
                        return chapterInfoEntity;
                    }).toList();

            chapterInfoService.updateBatchById(updateList);

            Path archiveWavPath = envConfig.buildProjectPath(
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
            Path outputSrtPath = envConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "output.srt");

            SubtitleUtils.srtFile(audioSegments, outputSrtPath);

            Path archiveSrtPath = envConfig.buildProjectPath(
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
    }

    @Override
    public Flux<String> roleInference(String projectId, String chapterId) {

        List<ChapterInfoEntity> chapterInfos = chapterInfoService.getByChapterId(chapterId);

        List<JSONObject> linesList = new ArrayList<>();
        chapterInfos.forEach(lineInfo -> {
            if (Objects.equals(lineInfo.getDialogueFlag(), Boolean.TRUE)) {
                JSONObject lines = new JSONObject();
                lines.put("台词序号", lineInfo.getIndex());
                lines.put("台词内容", lineInfo.getText());
                linesList.add(lines);
            }
        });

        if (CollectionUtils.isEmpty(linesList)) {
            return Flux.empty();
        }

        String lines = JSON.toJSONString(linesList);
        StringBuilder content = new StringBuilder();

        chapterInfos.stream()
                .collect(Collectors.groupingBy(ChapterInfoEntity::getParaIndex, TreeMap::new, Collectors.toList()))
                .values()
                .forEach(val -> {
                    val.stream().sorted(Comparator.comparingInt(ChapterInfoEntity::getSentIndex))
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

                台词列表部分：
                \{lines}

                原文部分：
                \{content.toString()}
                """;

        log.info("\n提示词, systemMessage: {}", systemMessage);
        log.info("\n提示词, userMessage: {}", userMessage);

        StringBuilder aiResultStr = new StringBuilder();
        AtomicBoolean isMapping = new AtomicBoolean(false);
        StringBuilder sbStr = new StringBuilder();

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

                    try {
                        sbStr.append(v);
                        while (true) {
                            int newlineIndex = sbStr.indexOf("\n");
                            if (newlineIndex == -1) {
                                break;
                            }
                            String line = sbStr.substring(0, newlineIndex + 1).trim();
                            sbStr.delete(0, newlineIndex + 1);

                            if (StringUtils.isNotBlank(line)) {
                                if (StringUtils.equals("roles:", line)) {
                                    isMapping.set(false);
                                    continue;
                                }
                                if (StringUtils.equals("linesMappings:", line)) {
                                    isMapping.set(true);
                                    continue;
                                }
                                if (!isMapping.get()) {
                                    String[] split = line.split(",");
                                    if (split.length == 3) {
                                        globalWebSocketHandler.sendSuccessMessage("角色推理", line);
                                    }
                                }
                                if (isMapping.get()) {
                                    String[] split = line.split(",");
                                    if (split.length == 3) {
                                        globalWebSocketHandler.sendSuccessMessage("情感推理", line);
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                })
                .doOnComplete(() -> mergeAiResultInfo(projectId, chapterId, aiResultStr.toString(), chapterInfos));
    }

    public void saveChapterInfoEntities(TextChapterEntity textChapter) {

        String projectId = textChapter.getProjectId();
        String chapterId = textChapter.getChapterId();
        String content = textChapter.getContent();
        String dialoguePattern = textChapter.getDialoguePattern();

        List<String> dialoguePatterns = StringUtils.isBlank(dialoguePattern)
                ? List.of()
                : List.of(dialoguePattern);

        List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

        int paraIndex = 0;

        for (String line : content.split("\n")) {
            int sentIndex = 0;

            List<Tuple2<Boolean, String>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

            for (Tuple2<Boolean, String> chapterInfoTuple2 : chapterInfoTuple2s) {

                ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                chapterInfoEntity.setProjectId(projectId);
                chapterInfoEntity.setChapterId(chapterId);
                chapterInfoEntity.setParaIndex(paraIndex);
                chapterInfoEntity.setSentIndex(sentIndex);
                chapterInfoEntity.setText(chapterInfoTuple2._2);
                chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._1);

                chapterInfoEntity.setRole("旁白");
                chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.init);

                chapterInfoEntities.add(chapterInfoEntity);

                sentIndex++;
            }

            paraIndex++;
        }

        chapterInfoService.deleteByChapterId(chapterId);
        textRoleService.deleteByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(chapterInfoEntities)) {

            List<TextCommonRoleEntity> commonRoleEntities = textCommonRoleService.getByProjectId(projectId);
            Optional<TextCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                    .filter(r -> StringUtils.equals(r.getRole(), "旁白"))
                    .findAny();

            TextRoleEntity textRoleEntity = new TextRoleEntity();
            textRoleEntity.setProjectId(projectId);
            textRoleEntity.setChapterId(chapterId);
            textRoleEntity.setRole("旁白");

            if (asideRoleOptional.isPresent()) {
                TextCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();

                textRoleEntity.setAudioModelInfo(textCommonRoleEntity);

                chapterInfoEntities = chapterInfoEntities.stream()
                        .peek(c -> c.setAudioModelInfo(textCommonRoleEntity)).toList();
            }

            chapterInfoService.saveBatch(chapterInfoEntities);
            textRoleService.save(textRoleEntity);

        }
    }

    public void mergeAiResultInfo(String projectId, String chapterId, String aiResultStr, List<ChapterInfoEntity> chapterInfos) {

        System.out.println("=========================文本大模型返回结果=========================");
        System.out.println(aiResultStr);
        System.out.println("=========================文本大模型返回结果=========================");

        try {

            AiResult aiResult = formatAiResult(aiResultStr);

            if (Objects.isNull(aiResult)) {
                globalWebSocketHandler.sendErrorMessage("没有接收到文本大模型的消息！");
            }

            aiResult = reCombineAiResult(aiResult);

            List<AiResult.Role> roles = aiResult.getRoles();

            Map<String, AiResult.Role> aiResultRoleMap = aiResult.getRoles()
                    .stream()
                    .collect(Collectors.toMap(AiResult.Role::getRole, Function.identity(), (a, _) -> a));
            Map<String, AiResult.LinesMapping> linesMappingMap = aiResult.getLinesMappings()
                    .stream()
                    .collect(Collectors.toMap(AiResult.LinesMapping::getLinesIndex, Function.identity(), (a, _) -> a));

            List<TextCommonRoleEntity> commonRoles = textCommonRoleService.list();
            Map<String, TextCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(TextCommonRoleEntity::getRole, Function.identity(), (a, _) -> a));

            List<Integer> audioModelResetIds = new ArrayList<>();
            boolean hasAside = false;
            for (ChapterInfoEntity chapterInfo : chapterInfos) {
                String key = chapterInfo.getIndex();
                String role = "旁白";
                if (linesMappingMap.containsKey(key)) {
                    AiResult.LinesMapping linesMapping = linesMappingMap.get(key);
                    role = linesMapping.getRole();
                } else {
                    hasAside = true;
                }

                chapterInfo.setRole(role);
                if (aiResultRoleMap.containsKey(role)) {
                    chapterInfo.setGender(aiResultRoleMap.get(role).getGender());
                    chapterInfo.setAge(aiResultRoleMap.get(role).getAge());
                }

                if (commonRoleMap.containsKey(role)) {
                    TextCommonRoleEntity commonRole = commonRoleMap.get(role);
                    chapterInfo.setAudioRoleInfo(commonRole);
                    chapterInfo.setAudioModelInfo(commonRole);

                    if (Objects.nonNull(commonRole.getGender())) {
                        chapterInfo.setGender(commonRole.getGender());
                    }

                    if (Objects.nonNull(commonRole.getAge())) {
                        chapterInfo.setAge(commonRole.getAge());
                    }

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
                        textRoleEntity.setAge(role.getAge());

                        TextCommonRoleEntity commonRole = commonRoleMap.get(role.getRole());
                        if (Objects.nonNull(commonRole)) {
                            if (Objects.isNull(textRoleEntity.getGender())) {
                                textRoleEntity.setGender(commonRole.getGender());
                            }
                            if (Objects.isNull(textRoleEntity.getAge())) {
                                textRoleEntity.setAge(commonRole.getAge());
                            }
                            textRoleEntity.setAudioModelInfo(commonRole);
                        }

                        return textRoleEntity;
                    }).toList();


            List<TextRoleInferenceEntity> roleInferenceEntities = aiResult.getLinesMappings().stream()
                    .map(linesMapping -> {
                        TextRoleInferenceEntity roleInferenceEntity = new TextRoleInferenceEntity();
                        roleInferenceEntity.setProjectId(projectId);
                        roleInferenceEntity.setChapterId(chapterId);
                        roleInferenceEntity.setTextIndex(linesMapping.getLinesIndex());
                        roleInferenceEntity.setRole(linesMapping.getRole());
                        roleInferenceEntity.setGender(aiResultRoleMap.get(linesMapping.getRole()).getGender());
                        roleInferenceEntity.setAge(aiResultRoleMap.get(linesMapping.getRole()).getAge());
                        roleInferenceEntity.setMood(linesMapping.getMood());
                        return roleInferenceEntity;
                    }).toList();

            chapterInfoService.updateBatchById(chapterInfos);
            chapterInfoService.audioModelReset(audioModelResetIds);

            textRoleService.deleteByChapterId(chapterId);
            textRoleService.saveBatch(textRoleEntities);

            textRoleInferenceService.deleteByChapterId(chapterId);
            textRoleInferenceService.saveBatch(roleInferenceEntities);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AiResult formatAiResult(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        boolean isMapping = false;
        List<AiResult.Role> roles = new ArrayList<>();
        List<AiResult.LinesMapping> linesMappings = new ArrayList<>();
        for (String line : text.split("\n")) {
            if (StringUtils.equals("roles:", line)) {
                isMapping = false;
                continue;
            }
            if (StringUtils.equals("linesMappings:", line)) {
                isMapping = true;
                continue;
            }
            if (!isMapping) {
                String[] split = line.split(",");
                if (split.length == 3) {
                    roles.add(new AiResult.Role(split[0], split[1], split[2]));
                }
            }
            if (isMapping) {
                String[] split = line.split(",");
                if (split.length == 3) {
                    linesMappings.add(new AiResult.LinesMapping(split[0], split[1], split[2]));
                }
            }
        }

        return new AiResult(roles, linesMappings);
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
}

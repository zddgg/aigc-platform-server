package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.bean.*;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;
import space.wenliang.ai.aigcplatformserver.service.*;
import space.wenliang.ai.aigcplatformserver.service.business.BChapterInfoService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.TextProjectWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.SubtitleUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Service
@RequiredArgsConstructor
public class BChapterInfoServiceImpl implements BChapterInfoService, StartHook.StartHookListener {

    public static final LinkedBlockingDeque<ChapterInfoEntity> audioCreateTaskQueue = new LinkedBlockingDeque<>();

    private final EnvConfig envConfig;
    private final AudioCreator audioCreator;

    private final TextRoleService textRoleService;
    private final TextProjectService textProjectService;
    private final TextChapterService textChapterService;
    private final ChapterInfoService chapterInfoService;
    private final TextCommonRoleService textCommonRoleService;

    private final AmModelFileService amModelFileService;
    private final AmModelConfigService amModelConfigService;
    private final AmPromptAudioService amPromptAudioService;

    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final TextProjectWebSocketHandler textProjectWebSocketHandler;
    private final GlobalSettingService globalSettingService;

    @Override
    public void startHook() {
        audioCreateTask();
    }

    @Override
    public List<ChapterInfoEntity> chapterInfos(String projectId, String chapterId) {

        List<ChapterInfoEntity> chapterInfos = chapterInfoService.getByChapterId(chapterId);

        if (CollectionUtils.isEmpty(chapterInfos)) {
            TextChapterEntity textChapterEntity = textChapterService.getByChapterId(chapterId);

            List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.buildChapterInfos(textChapterEntity);

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
                            .peek(c -> {
                                c.setAmType(textCommonRoleEntity.getAmType());
                                c.setModelFile(amModelFileService.getByMfId(textCommonRoleEntity.getAmMfId()));
                                c.setModelConfig(amModelConfigService.getByMcId(textCommonRoleEntity.getAmMcId()));
                                c.setPromptAudio(amPromptAudioService.getByPaId(textCommonRoleEntity.getAmPaId()));
                            }).toList();
                }

                chapterInfoService.saveBatch(chapterInfoEntities);
                textRoleService.save(textRoleEntity);

                JSONObject j1 = new JSONObject();
                j1.put("type", "chapter_reload");
                j1.put("state", "success");
                j1.put("projectId", projectId);
                j1.put("chapterId", chapterId);

                textProjectWebSocketHandler.sendMessageToProject(projectId, JSON.toJSONString(j1));
            } else {
                return new ArrayList<>();
            }

            chapterInfos = chapterInfoEntities;
        }

        return chapterInfos;
    }

    @Override
    public void chapterInfoSort(List<ChapterInfoEntity> chapterInfoEntities) {
        List<ChapterInfoEntity> updateList = chapterInfoEntities.stream().map(c -> {
            ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
            chapterInfoEntity.setId(c.getId());
            chapterInfoEntity.setTextSort(c.getTextSort());
            return chapterInfoEntity;
        }).toList();
        chapterInfoService.updateBatchById(updateList);
    }

    @Override
    public void audioModelChange(UpdateModelInfo updateModelInfo) {

        List<ChapterInfoEntity> updateList = updateModelInfo.getIds().stream().map(id -> {
            ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
            chapterInfoEntity.setId(id);
            chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.modified);

            chapterInfoEntity.setAmType(updateModelInfo.getAmType());
            chapterInfoEntity.setModelFile(amModelFileService.getByMfId(updateModelInfo.getAmMfId()));
            chapterInfoEntity.setModelConfig(amModelConfigService.getByMcId(updateModelInfo.getAmMcId()));
            chapterInfoEntity.setPromptAudio(amPromptAudioService.getByPaId(updateModelInfo.getAmPaId()));

            if (StringUtils.isNotBlank(updateModelInfo.getAmMcParamsJson())) {
                chapterInfoEntity.setAmMcParamsJson(updateModelInfo.getAmMcParamsJson());
            }

            return chapterInfoEntity;
        }).toList();

        chapterInfoService.updateBatchById(updateList);
    }

    @Override
    public void updateVolume(ChapterInfoEntity chapterInfoEntity) {
        chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioVolume, chapterInfoEntity.getAudioVolume())
                .set(ChapterInfoEntity::getAudioTaskState, AudioTaskStateConstants.modified)
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateSpeed(ChapterInfoEntity chapterInfoEntity) {
        chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioSpeed, chapterInfoEntity.getAudioSpeed())
                .set(ChapterInfoEntity::getAudioTaskState, AudioTaskStateConstants.modified)
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateInterval(ChapterInfoEntity chapterInfoEntity) {
        chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioInterval, chapterInfoEntity.getAudioInterval())
                .set(ChapterInfoEntity::getAudioTaskState, AudioTaskStateConstants.modified)
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void updateControls(ControlsUpdate controlsUpdate) {
        List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.listByIds(controlsUpdate.getChapterInfoIds());
        for (ChapterInfoEntity chapterInfoEntity : chapterInfoEntities) {
            if (Objects.equals(controlsUpdate.getEnableVolume(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioVolume(controlsUpdate.getVolume());
            }
            if (Objects.equals(controlsUpdate.getEnableSpeed(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioSpeed(controlsUpdate.getSpeed());
            }
            if (Objects.equals(controlsUpdate.getEnableInterval(), Boolean.TRUE)) {
                chapterInfoEntity.setAudioInterval(controlsUpdate.getInterval());
            }
        }

        chapterInfoService.updateBatchById(chapterInfoEntities);
    }

    @Override
    public void updateChapterText(ChapterInfoEntity chapterInfoEntity) {
        chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getText, chapterInfoEntity.getText())
                .set(ChapterInfoEntity::getAudioTaskState, AudioTaskStateConstants.modified)
                .set(ChapterInfoEntity::getTextMarkupInfoJson, null)
                .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
    }

    @Override
    public void deleteChapterInfo(ChapterInfoEntity chapterInfoEntity) {
        chapterInfoService.removeById(chapterInfoEntity.getId());
    }

    @Override
    public void addAudioCreateTask(ChapterInfoEntity chapterInfoEntity) {
        ChapterInfoEntity chapterInfo = chapterInfoService.getById(chapterInfoEntity.getId());
        if (Objects.nonNull(chapterInfo)) {
            audioCreateTaskQueue.add(chapterInfo);
        }

        sendAudioGenerateSummaryMsg(chapterInfoEntity.getProjectId(), chapterInfoEntity.getChapterId());
    }

    @Override
    public void startCreateAudio(String projectId, String chapterId, String actionType) {
        List<ChapterInfoEntity> entities = chapterInfoService.getByChapterId(chapterId)
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getAmType()))
                .filter(c -> {
                    if (StringUtils.equals(actionType, "all")) {
                        return true;
                    }
                    return !List.of(AudioTaskStateConstants.created, AudioTaskStateConstants.combined).contains(c.getAudioTaskState());
                })
                .toList();
        audioCreateTaskQueue.addAll(entities);

        sendAudioGenerateSummaryMsg(projectId, chapterId);
    }

    @Override
    public void stopCreateAudio() {
        audioCreateTaskQueue.clear();
    }

    @Override
    public List<ChapterInfoEntity> chapterCondition(String projectId, String chapterId) {
        return chapterInfoService.getByChapterId(chapterId);
    }

    @Override
    public void addPolyphonicInfo(PolyphonicParams polyphonicParams) {
        ChapterInfoEntity chapterInfoEntity = chapterInfoService.getById(polyphonicParams.getChapterInfoId());
        if (Objects.nonNull(chapterInfoEntity)) {
            TextMarkupInfo textMarkupInfo = chapterInfoEntity.getTextMarkupInfo();
            List<PolyphonicInfo> polyphonicInfos = textMarkupInfo.getPolyphonicInfos();

            boolean exist = false;
            for (PolyphonicInfo polyphonicInfo : polyphonicInfos) {
                if (Objects.equals(polyphonicInfo.getIndex(), polyphonicParams.getIndex())) {
                    polyphonicInfo.setMarkup(polyphonicParams.getMarkup());
                    exist = true;
                }
            }
            if (!exist) {
                polyphonicInfos.add(polyphonicParams);
            }

            textMarkupInfo.setPolyphonicInfos(polyphonicInfos);
            chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                    .set(ChapterInfoEntity::getTextMarkupInfoJson, JSON.toJSONString(textMarkupInfo))
                    .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
        }
    }

    @Override
    public void removePolyphonicInfo(PolyphonicParams polyphonicParams) {
        ChapterInfoEntity chapterInfoEntity = chapterInfoService.getById(polyphonicParams.getChapterInfoId());
        if (Objects.nonNull(chapterInfoEntity)) {
            TextMarkupInfo textMarkupInfo = chapterInfoEntity.getTextMarkupInfo();
            List<PolyphonicInfo> polyphonicInfos = textMarkupInfo.getPolyphonicInfos();

            polyphonicInfos = polyphonicInfos.stream()
                    .filter(v -> !Objects.equals(polyphonicParams.getIndex(), v.getIndex()))
                    .toList();

            textMarkupInfo.setPolyphonicInfos(polyphonicInfos);
            chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                    .set(ChapterInfoEntity::getTextMarkupInfoJson, JSON.toJSONString(textMarkupInfo))
                    .eq(ChapterInfoEntity::getId, chapterInfoEntity.getId()));
        }
    }

    @Override
    public ChapterInfoEntity addChapterInfo(ChapterInfoEntity chapterInfo) {
        ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
        chapterInfoEntity.setProjectId(chapterInfo.getProjectId());
        chapterInfoEntity.setChapterId(chapterInfo.getChapterId());
        chapterInfoEntity.setText(chapterInfo.getText());
        chapterInfoEntity.setTextSort(chapterInfo.getTextSort());
        chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.init);
        String aside = "旁白";
        chapterInfoEntity.setRole(aside);


        List<TextCommonRoleEntity> commonRoleEntities = textCommonRoleService.getByProjectId(chapterInfo.getProjectId());
        Optional<TextCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                .filter(r -> StringUtils.equals(r.getRole(), aside))
                .findAny();

        if (asideRoleOptional.isPresent()) {
            TextCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();

            chapterInfoEntity.setAudioRoleInfo(textCommonRoleEntity);
            chapterInfoEntity.setAudioModelInfo(textCommonRoleEntity);
        }

        TextRoleEntity textRole = textRoleService.getOne(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, chapterInfo.getProjectId())
                .eq(TextRoleEntity::getChapterId, chapterInfo.getChapterId())
                .eq(TextRoleEntity::getRole, aside));

        if (Objects.nonNull(textRole)) {
            chapterInfoEntity.setAudioRoleInfo(textRole);
            chapterInfoEntity.setAudioModelInfo(textRole);
        }

        List<ChapterInfoEntity> chapterInfoEntities = chapterInfoService.getByChapterId(chapterInfo.getChapterId());
        int i = 0;
        int maxParaIndex = 0;
        List<ChapterInfoEntity> updateList = new ArrayList<>();
        for (ChapterInfoEntity infoEntity : chapterInfoEntities) {
            ChapterInfoEntity save = new ChapterInfoEntity();
            save.setId(infoEntity.getId());
            if (i >= Optional.ofNullable(chapterInfo.getTextSort()).orElse(0)) {
                save.setTextSort(i + 1);
            } else {
                save.setTextSort(i);
            }
            i++;

            updateList.add(save);

            maxParaIndex = Math.max(maxParaIndex, infoEntity.getParaIndex());
        }

        chapterInfoEntity.setParaIndex(maxParaIndex + 1);
        chapterInfoEntity.setSentIndex(0);

        chapterInfoService.updateBatchById(updateList);
        chapterInfoService.save(chapterInfoEntity);

        return chapterInfoEntity;
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

    private void createAudio(ChapterInfoEntity chapterInfo) {

        try {

            AudioContext audioContext = new AudioContext();
            audioContext.setAudioModelInfo(chapterInfo);
            audioContext.setTextMarkupInfo(chapterInfo.getTextMarkupInfo());

            TextProjectEntity textProject = textProjectService.getByProjectId(chapterInfo.getProjectId());
            TextChapterEntity textChapter = textChapterService.getByChapterId(chapterInfo.getChapterId());

            Path outputDir = envConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "audio");

            audioContext.setOutputDir(outputDir.toAbsolutePath().toString());

            Boolean subtitleOptimize = globalSettingService.getGlobalSetting().getSubtitleOptimize();

            List<String> subTexts = SubtitleUtils.subtitleSplit(chapterInfo.getText(), subtitleOptimize);

            List<String> outputFileNames = new ArrayList<>();

            int textPartIndexStart = 0;
            for (int i = 0; i < subTexts.size(); i++) {
                audioContext.setText(subTexts.get(i));

                int textPartIndexEnd = textPartIndexStart + subTexts.get(i).length() - 1;

                audioContext.setTextPartIndexStart(textPartIndexStart);
                audioContext.setTextPartIndexEnd(textPartIndexEnd);

                String outputName = chapterInfo.getIndex() + "-" + i;
                audioContext.setOutputName(outputName);

                audioCreator.createFile(audioContext);

                outputFileNames.add(outputName + "." + audioContext.getMediaType());

                textPartIndexStart = textPartIndexEnd + 1;
            }

            chapterInfoService.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                    .set(ChapterInfoEntity::getAudioFiles, String.join(",", outputFileNames))
                    .set(ChapterInfoEntity::getAudioTaskState, AudioTaskStateConstants.created)
                    .eq(ChapterInfoEntity::getId, chapterInfo.getId()));

            // 查询最新的 ChapterInfo
            chapterInfo = chapterInfoService.getById(chapterInfo.getId());

            JSONObject j1 = new JSONObject();
            j1.put("type", "audio_generate_result");
            j1.put("state", "success");
            j1.put("projectId", chapterInfo.getProjectId());
            j1.put("chapterId", chapterInfo.getChapterId());
            j1.put("chapterInfo", chapterInfo);

            textProjectWebSocketHandler.sendMessageToProject(chapterInfo.getProjectId(), JSON.toJSONString(j1));

        } catch (Exception e) {
            log.error("Create Audio Failed", e);
            globalWebSocketHandler.sendErrorMessage(chapterInfo.getIndex() + " 音频生成失败", e.getMessage());
        } finally {
            sendAudioGenerateSummaryMsg(chapterInfo.getProjectId(), chapterInfo.getChapterId());
        }
    }

    private void sendAudioGenerateSummaryMsg(String projectId, String chapterId) {
        JSONObject j1 = new JSONObject();
        j1.put("type", "audio_generate_summary");
        j1.put("state", "success");
        j1.put("projectId", projectId);
        j1.put("chapterId", chapterId);
        j1.put("taskNum", audioCreateTaskQueue.size());

        List<String> creatingIds = new ArrayList<>();
        audioCreateTaskQueue.forEach(t -> creatingIds.add(t.getIndex()));
        j1.put("creatingIds", creatingIds);
        textProjectWebSocketHandler.sendMessageToProject(projectId, JSON.toJSONString(j1));
    }
}

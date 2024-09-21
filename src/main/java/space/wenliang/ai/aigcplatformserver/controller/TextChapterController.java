package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.*;
import space.wenliang.ai.aigcplatformserver.common.Page;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.service.ChapterInfoService;
import space.wenliang.ai.aigcplatformserver.service.TextChapterService;
import space.wenliang.ai.aigcplatformserver.service.TextProjectService;
import space.wenliang.ai.aigcplatformserver.service.business.BChapterInfoService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.util.AudioUtils;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.SubtitleUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("textChapter")
@RequiredArgsConstructor
public class TextChapterController {

    private final EnvConfig envConfig;
    private final TextChapterService textChapterService;
    private final TextProjectService textProjectService;
    private final ChapterInfoService chapterInfoService;
    private final BTextChapterService bTextChapterService;
    private final BChapterInfoService bChapterInfoService;
    private final GlobalSettingService globalSettingService;

    @PostMapping("pageChapters")
    public Result<Object> pageChapters(@RequestBody ProjectQuery projectQuery) {
        Page<TextChapterEntity> page = bTextChapterService.pageChapters(projectQuery);
        return Result.success(page);
    }

    @PostMapping("chapters4Sort")
    public Result<List<TextChapterEntity>> chapters4Sort(@SingleValueParam("projectId") String projectId) {
        List<TextChapterEntity> list = bTextChapterService.chapters4Sort(projectId);
        return Result.success(list);
    }

    @PostMapping(value = "deleteChapter")
    public Result<Object> deleteChapter(@RequestBody TextChapterEntity textChapter) throws IOException {
        bTextChapterService.deleteChapter(textChapter);
        return Result.success();
    }

    @PostMapping("getTextChapter")
    public Result<Object> getContent(@SingleValueParam("projectId") String projectId,
                                     @SingleValueParam("chapterId") String chapterId) {
        TextChapterEntity textChapter = textChapterService.getTextChapterAndContent(projectId, chapterId);
        if (Objects.nonNull(textChapter)) {
            Map<String, ChapterSummary> chapterSummaryMap = chapterInfoService.chapterSummaryMap();
            ChapterSummary chapterSummary = chapterSummaryMap.get(chapterId);
            if (Objects.nonNull(chapterSummary)) {
                textChapter.setAudioTaskState(chapterSummary.getMaxTaskState());
            }
        }
        return Result.success(textChapter);
    }

    @PostMapping("tmpDialogueParse")
    public Result<Object> tmpDialogueParse(@RequestBody TextChapterEntity textChapter) {
        List<ChapterInfoEntity> chapterInfoEntities =
                bTextChapterService.tmpDialogueParse(textChapter);
        return Result.success(chapterInfoEntities);
    }

    @PostMapping("chapterEdit")
    public Result<Object> chapterEdit(@RequestBody TextChapterEntity textChapter) {
        bTextChapterService.chapterEdit(textChapter);
        return Result.success();
    }

    @PostMapping("chapterAdd")
    public Result<Object> chapterAdd(@RequestBody ChapterAdd chapterAdd) {
        bTextChapterService.chapterAdd(chapterAdd);
        return Result.success();
    }

    @PostMapping("chapterSort")
    public Result<Object> chapterSort(@RequestBody List<TextChapterEntity> sortChapters) {
        bTextChapterService.chapterSort(sortChapters);
        return Result.success();
    }

    @PostMapping("chapterInfos")
    public Result<Object> chapterInfos(@SingleValueParam("projectId") String projectId,
                                       @SingleValueParam("chapterId") String chapterId) {
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(chapterId)) {
            return Result.success(new ArrayList<>());
        }
        List<ChapterInfoEntity> list = bChapterInfoService.chapterInfos(projectId, chapterId);
        return Result.success(list);
    }

    @PostMapping("chapterInfoSort")
    public Result<Object> chapterInfoSort(@RequestBody List<ChapterInfoEntity> chapterInfoEntities) {
        bChapterInfoService.chapterInfoSort(chapterInfoEntities);
        return Result.success();
    }

    @PostMapping("roles")
    public Result<Object> roles(@SingleValueParam("chapterId") String chapterId) {
        List<TextRoleEntity> list = bTextChapterService.roles(chapterId);
        return Result.success(list);
    }

    @PostMapping("updateRole")
    public Result<Object> updateRole(@RequestBody TextRoleEntity textRoleEntity) {
        bTextChapterService.updateRole(textRoleEntity);
        return Result.success();
    }

    @PostMapping("updateRoleModel")
    public Result<Object> updateRoleModel(@RequestBody UpdateModelInfo updateModelInfo) {
        bTextChapterService.updateRoleModel(updateModelInfo);
        return Result.success();
    }

    @PostMapping("roleCombine")
    public Result<Object> roleCombine(@SingleValueParam("projectId") String projectId,
                                      @SingleValueParam("chapterId") String chapterId,
                                      @SingleValueParam("fromRoleName") String fromRoleName,
                                      @SingleValueParam("toRoleName") String toRoleName) {
        bTextChapterService.roleCombine(projectId, chapterId, fromRoleName, toRoleName);
        return Result.success();
    }

    @PostMapping("textRoleChange")
    public Result<Object> textRoleChange(@RequestBody TextRoleChange textRoleChange) {
        bTextChapterService.textRoleChange(textRoleChange);
        return Result.success();
    }

    @PostMapping("saveToCommonRole")
    public Result<Object> saveToCommonRole(@RequestBody TextRoleEntity textRoleEntity) {
        Boolean result = bTextChapterService.saveToCommonRole(textRoleEntity);
        return Result.success(result);
    }

    @PostMapping("commonRoles")
    public Result<Object> commonRoles(@SingleValueParam("projectId") String projectId) {
        List<TextCommonRoleEntity> list = bTextChapterService.commonRoles(projectId);
        return Result.success(list);
    }

    @PostMapping("createCommonRole")
    public Result<Object> createCommonRole(@RequestBody TextCommonRoleEntity textCommonRoleEntity) {
        bTextChapterService.createCommonRole(textCommonRoleEntity);
        return Result.success();
    }

    @PostMapping("updateCommonRole")
    public Result<Object> updateCommonRole(@RequestBody UpdateModelInfo updateModelInfo) {
        bTextChapterService.updateCommonRole(updateModelInfo);
        return Result.success();
    }

    @PostMapping("deleteCommonRole")
    public Result<Object> deleteCommonRole(@RequestBody TextCommonRoleEntity textCommonRoleEntity) {
        bTextChapterService.deleteCommonRole(textCommonRoleEntity);
        return Result.success();
    }

    @PostMapping("checkRoleInference")
    public Result<Object> checkRoleInference(@SingleValueParam("projectId") String projectId,
                                             @SingleValueParam("chapterId") String chapterId) {
        return Result.success(bTextChapterService.checkRoleInference(projectId, chapterId));
    }

    @PostMapping("loadRoleInference")
    public Result<Object> loadRoleInference(@SingleValueParam("projectId") String projectId,
                                            @SingleValueParam("chapterId") String chapterId) {
        bTextChapterService.loadRoleInference(projectId, chapterId);
        return Result.success();
    }

    @PostMapping(value = "audioModelChange")
    public Result<Object> audioModelChange(@RequestBody UpdateModelInfo updateModelInfo) {
        bChapterInfoService.audioModelChange(updateModelInfo);
        return Result.success();
    }

    @PostMapping(value = "updateVolume")
    public Result<Object> updateVolume(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.updateVolume(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "updateSpeed")
    public Result<Object> updateSpeed(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.updateSpeed(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "updateInterval")
    public Result<Object> updateInterval(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.updateInterval(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "updateControls")
    public Result<Object> updateControls(@RequestBody ControlsUpdate controlsUpdate) {
        bChapterInfoService.updateControls(controlsUpdate);
        return Result.success();
    }

    @PostMapping(value = "updateChapterText")
    public Result<Object> updateChapterText(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.updateChapterText(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "deleteChapterInfo")
    public Result<Object> deleteChapterInfo(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.deleteChapterInfo(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "createAudio")
    public Result<Object> createAudio(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.addAudioCreateTask(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "startCreateAudio")
    public Result<Object> startCreateAudio(@SingleValueParam("projectId") String projectId,
                                           @SingleValueParam("chapterId") String chapterId,
                                           @SingleValueParam("actionType") String actionType,
                                           @SingleValueParam("chapterInfoIds") List<Integer> chapterInfoIds) {
        bChapterInfoService.startCreateAudio(projectId, chapterId, actionType, chapterInfoIds);
        return Result.success();
    }

    @PostMapping(value = "stopCreateAudio")
    public Result<Object> stopCreateAudio() {
        bChapterInfoService.stopCreateAudio();
        return Result.success();
    }

    @PostMapping(value = "chapterExpose")
    public Result<Object> chapterExpose(@RequestBody ChapterExpose chapterExpose) throws Exception {
        bTextChapterService.chapterExpose(chapterExpose);
        return Result.success();
    }

    @PostMapping(value = "addChapterInfo")
    public Result<Object> addChapterInfo(@RequestBody ChapterInfoEntity chapterInfo) {
        ChapterInfoEntity chapterInfoEntity = bChapterInfoService.addChapterInfo(chapterInfo);
        return Result.success(chapterInfoEntity);
    }

    @PostMapping("playAudio")
    public ResponseEntity<byte[]> playAudio(@RequestBody ChapterInfoEntity chapterInfoEntity) throws Exception {

        ChapterInfoEntity chapterInfo = chapterInfoService.getById(chapterInfoEntity.getId());

        TextProjectEntity textProject = textProjectService.getByProjectId(chapterInfo.getProjectId());
        TextChapterEntity textChapter = textChapterService.getByChapterId(chapterInfo.getChapterId());

        Boolean subtitleOptimize = globalSettingService.getGlobalSetting().getSubtitleOptimize();

        List<String> subtitles = SubtitleUtils.subtitleSplit(chapterInfo.getText(), subtitleOptimize);

        String[] audioNames = chapterInfo.getAudioFiles().split(",");

        if (CollectionUtils.isEmpty(subtitles) || subtitles.size() != audioNames.length) {
            return null;
        }

        List<AudioSegment> audioSegments = new ArrayList<>();

        for (int i = 0; i < audioNames.length; i++) {

            AudioSegment subAudioSegment = new AudioSegment();
            subAudioSegment.setId(chapterInfo.getId());
            subAudioSegment.setPart(i);
            subAudioSegment.setAudioName(audioNames[i]);
            subAudioSegment.setText(subtitles.get(i));
            subAudioSegment.setAudioVolume(chapterInfo.getAudioVolume());
            subAudioSegment.setAudioSpeed(chapterInfo.getAudioSpeed());

            if (i == audioNames.length - 1) {
                subAudioSegment.setAudioInterval(chapterInfo.getAudioInterval());
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

            audioSegments.add(subAudioSegment);
        }

        if (!CollectionUtils.isEmpty(audioSegments)) {
            Path path = envConfig.buildProjectPath(
                    "text",
                    FileUtils.fileNameFormat(textProject.getProjectName()),
                    FileUtils.fileNameFormat(textChapter.getChapterName()),
                    "audio",
                    chapterInfo.getIndex() + ".wav");

            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            AudioUtils.mergeAudioFiles(audioSegments, path.toAbsolutePath().toString());
            return ResponseEntity.ok().body(Files.readAllBytes(path));
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "chapterCondition")
    public Result<Object> chapterCondition(@SingleValueParam("projectId") String projectId,
                                           @SingleValueParam("chapterId") String chapterId) {
        List<ChapterInfoEntity> chapterInfos = bChapterInfoService.chapterCondition(projectId, chapterId);
        return Result.success(chapterInfos);
    }

    @PostMapping(value = "getChapterAudio")
    public ResponseEntity<byte[]> getChapterAudio(@SingleValueParam("projectId") String projectId,
                                                  @SingleValueParam("chapterId") String chapterId) throws IOException {

        TextProjectEntity textProject = textProjectService.getByProjectId(projectId);
        TextChapterEntity textChapter = textChapterService.getByChapterId(chapterId);

        Path audioPath = envConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "output.wav");

        if (Files.exists(audioPath)) {
            return ResponseEntity.ok().body(Files.readAllBytes(audioPath));
        }

        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "getChapterSubtitle")
    public Result<Object> getChapterSubtitle(@SingleValueParam("projectId") String projectId,
                                             @SingleValueParam("chapterId") String chapterId) throws IOException {

        TextProjectEntity textProject = textProjectService.getByProjectId(projectId);
        TextChapterEntity textChapter = textChapterService.getByChapterId(chapterId);

        Path srtPath = envConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "output.srt");

        List<Subtitle> subtitles = new ArrayList<>();
        if (Files.exists(srtPath)) {
            subtitles = SubtitleUtils.readSrtFile(srtPath);
        }

        return Result.success(subtitles);
    }

    @PostMapping(value = "addPolyphonicInfo")
    public Result<Object> addPolyphonicInfo(@RequestBody PolyphonicParams polyphonicParams) {
        bChapterInfoService.addPolyphonicInfo(polyphonicParams);
        return Result.success();
    }

    @PostMapping(value = "removePolyphonicInfo")
    public Result<Object> removePolyphonicInfo(@RequestBody PolyphonicParams polyphonicParams) {
        bChapterInfoService.removePolyphonicInfo(polyphonicParams);
        return Result.success();
    }

    @PostMapping(value = "batchOperator")
    public Result<Object> batchOperator(@RequestBody ChapterBatchOperator ChapterBatchOperator) {
        bChapterInfoService.batchOperator(ChapterBatchOperator);
        return Result.success();
    }
}

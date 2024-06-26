package space.wenliang.ai.aigcplatformserver.controller;

import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.ChapterExpose;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.business.BChapterInfoService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("textChapter")
public class TextChapterController {

    private final BTextChapterService bTextChapterService;
    private final BChapterInfoService bChapterInfoService;

    public TextChapterController(BTextChapterService bTextChapterService,
                                 BChapterInfoService bChapterInfoService) {
        this.bTextChapterService = bTextChapterService;
        this.bChapterInfoService = bChapterInfoService;
    }

    @PostMapping("chapters")
    public Result<List<TextChapterEntity>> chapters(@SingleValueParam("projectId") String projectId) {
        List<TextChapterEntity> list = bTextChapterService.chapters(projectId);
        return Result.success(list);
    }

    @PostMapping("getContent")
    public Result<Object> getContent(@SingleValueParam("projectId") String projectId,
                                     @SingleValueParam("chapterId") String chapterId) {
        String content = bTextChapterService.getContent(projectId, chapterId);
        return Result.success(content);
    }

    @PostMapping("tmpDialogueParse")
    public Result<Object> tmpDialogueParse(@SingleValueParam("projectId") String projectId,
                                           @SingleValueParam("chapterId") String chapterId,
                                           @SingleValueParam("dialoguePattern") String dialoguePattern,
                                           @SingleValueParam("textContent") String textContent) {
        List<ChapterInfoEntity> chapterInfoEntities =
                bTextChapterService.tmpDialogueParse(projectId, chapterId, dialoguePattern, textContent);
        return Result.success(chapterInfoEntities);
    }

    @PostMapping("dialogueParse")
    public Result<Object> dialogueParse(@SingleValueParam("projectId") String projectId,
                                        @SingleValueParam("chapterId") String chapterId,
                                        @SingleValueParam("dialoguePattern") String dialoguePattern,
                                        @SingleValueParam("textContent") String textContent) {
        bTextChapterService.dialogueParse(projectId, chapterId, dialoguePattern, textContent);
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

    @PostMapping("roles")
    public Result<Object> roles(@SingleValueParam("projectId") String projectId,
                                @SingleValueParam("chapterId") String chapterId) {
        List<TextRoleEntity> list = bTextChapterService.roles(projectId, chapterId);
        return Result.success(list);
    }

    @PostMapping("updateRoleName")
    public Result<Object> updateRoleName(@RequestBody TextRoleEntity textRoleEntity) {
        bTextChapterService.updateRoleName(textRoleEntity);
        return Result.success();
    }

    @PostMapping("updateRole")
    public Result<Object> updateRole(@RequestBody TextRoleEntity textRoleEntity) {
        bTextChapterService.updateRole(textRoleEntity);
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
    public Result<Object> updateCommonRole(@RequestBody TextCommonRoleEntity textCommonRoleEntity) {
        bTextChapterService.updateCommonRole(textCommonRoleEntity);
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
    public Result<Object> audioModelChange(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        bChapterInfoService.audioModelChange(chapterInfoEntity);
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

    @PostMapping(value = "createAudio")
    public Result<List<String>> createAudio(@RequestBody ChapterInfoEntity chapterInfoEntity) {
        List<String> creatingIds = bChapterInfoService.addAudioCreateTask(chapterInfoEntity);

        return Result.success(creatingIds).setMsg("提交任务数：1");
    }

    @PostMapping(value = "startCreateAudio")
    public Result<List<String>> startCreateAudio(@SingleValueParam("projectId") String projectId,
                                                 @SingleValueParam("chapterId") String chapterId,
                                                 @SingleValueParam("actionType") String actionType) {
        Tuple2<Integer, List<String>> tuple2 = bChapterInfoService.startCreateAudio(projectId, chapterId, actionType);

        return Result.success(tuple2._2).setMsg("提交任务数：" + tuple2._1);
    }

    @PostMapping(value = "stopCreateAudio")
    public Result<Object> stopCreateAudio() {
        bChapterInfoService.stopCreateAudio();
        return Result.success();
    }

    @PostMapping(value = "chapterExpose")
    public Result<Object> chapterExpose(@RequestBody ChapterExpose chapterExpose) {
        bTextChapterService.chapterExpose(chapterExpose);
        return Result.success();
    }

}

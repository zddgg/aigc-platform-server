package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.FormatTextProject;
import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.service.TextProjectService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextProjectService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("textProject")
@RequiredArgsConstructor
public class TextProjectController {

    private final TextProjectService textProjectService;
    private final BTextProjectService bTextProjectService;

    @PostMapping("projectList")
    public Result<Object> projectList() {
        List<TextProject> list = bTextProjectService.projectList();
        return Result.success(list);
    }

    @PostMapping("getTextProject")
    public Result<Object> getTextProject(@SingleValueParam("projectId") String projectId) {
        TextProjectEntity textProject = bTextProjectService.getByProjectId(projectId);
        return Result.success(textProject);
    }

    @PostMapping("createProject")
    public Result<Object> createProject(@RequestParam("project") String project,
                                        @RequestParam("projectType") String projectType,
                                        @RequestParam("file") MultipartFile file) throws IOException {

        Charset charset = FileUtils.detectCharset(file.getBytes());

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    content.append(line.stripLeading()).append("\n");
                }
            }
        }
        bTextProjectService.createProject(project, projectType, content.toString());
        return Result.success();
    }

    @PostMapping("createFormatTextProject")
    public Result<Object> createFormatTextProject(@RequestBody FormatTextProject project) {
        bTextProjectService.createFormatTextProject(project);
        return Result.success();
    }

    @PostMapping("updateProject")
    public Result<Object> updateProject(@RequestBody TextProjectEntity textProjectEntity) throws IOException {
        bTextProjectService.updateProject(textProjectEntity);
        return Result.success();
    }


    @PostMapping("deleteProject")
    public Result<Object> deleteProject(@RequestBody TextProjectEntity textProjectEntity) throws IOException {
        bTextProjectService.deleteProject(textProjectEntity);
        return Result.success();
    }

    @PostMapping("tmpChapterSplit")
    public Result<Object> tmpChapterSplit(@SingleValueParam("projectId") String projectId,
                                          @SingleValueParam("chapterPattern") String chapterPattern,
                                          @SingleValueParam("dialoguePattern") String dialoguePattern) {
        List<String> chapterTitles = bTextProjectService.tmpChapterSplit(projectId, chapterPattern, dialoguePattern);
        return Result.success(chapterTitles);
    }

    @PostMapping("chapterSplit")
    public Result<Object> chapterSplit(@SingleValueParam("projectId") String projectId,
                                       @SingleValueParam("chapterPattern") String chapterPattern,
                                       @SingleValueParam("dialoguePattern") String dialoguePattern) {
        bTextProjectService.chapterSplit(projectId, chapterPattern, dialoguePattern);
        return Result.success();
    }
}

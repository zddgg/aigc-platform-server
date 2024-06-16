package space.wenliang.ai.aigcplatformserver.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
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
public class TextProjectController {

    private final BTextProjectService bTextProjectService;

    public TextProjectController(BTextProjectService bTextProjectService) {
        this.bTextProjectService = bTextProjectService;
    }

    @PostMapping("list")
    public Result<List<TextProject>> create() {
        List<TextProject> list = bTextProjectService.list();
        return Result.success(list);
    }

    @PostMapping("create")
    public Result<Object> create(@RequestParam("project") String project,
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
        bTextProjectService.create(project, content.toString());
        return Result.success();
    }


    @PostMapping("update")
    public Result<Object> update(@RequestBody TextProjectEntity textProjectEntity) throws IOException {
        bTextProjectService.update(textProjectEntity);
        return Result.success();
    }

    @PostMapping("delete")
    public Result<Object> delete(@RequestBody TextProjectEntity textProjectEntity) throws IOException {
        bTextProjectService.delete(textProjectEntity);
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

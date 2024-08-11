package space.wenliang.ai.aigcplatformserver.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.wenliang.ai.aigcplatformserver.bean.OpenFolder;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.service.TextChapterService;
import space.wenliang.ai.aigcplatformserver.service.TextProjectService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesController {

    private final EnvConfig envConfig;
    private final ServletContext servletContext;
    private final TextProjectService textProjectService;
    private final TextChapterService textChapterService;

    @GetMapping("/model/**")
    public ResponseEntity<Resource> getModelFile(HttpServletRequest request) {
        return getResourceFile(request, "/files/model/", envConfig.getModelDir());
    }

    @GetMapping("/project/**")
    public ResponseEntity<Resource> getProjectFile(HttpServletRequest request) {
        return getResourceFile(request, "/files/project/", envConfig.getProjectDir());
    }

    @PostMapping("openFolder")
    public Result<Object> openFolder(@RequestBody OpenFolder openFolder) throws IOException {
        TextProjectEntity textProject = textProjectService.getByProjectId(openFolder.getProjectId());
        TextChapterEntity textChapter = textChapterService.getByChapterId(openFolder.getChapterId());

        Path srtPath = envConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()));
        FileUtils.openFolder(srtPath);
        return Result.success();
    }

    public ResponseEntity<Resource> getResourceFile(HttpServletRequest request, String subUrl, String dir) {
        try {

            String fullPath = URLDecoder.decode(request.getRequestURI().substring(subUrl.length()), StandardCharsets.UTF_8);
            Path filePath = Paths.get(dir).resolve(fullPath).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // 确定文件的MIME类型
            String contentType = servletContext.getMimeType(filePath.toString());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 设置响应头并返回文件
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

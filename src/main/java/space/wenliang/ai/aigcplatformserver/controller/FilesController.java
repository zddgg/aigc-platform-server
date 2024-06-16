package space.wenliang.ai.aigcplatformserver.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final PathConfig pathConfig;
    private final ServletContext servletContext;

    public FilesController(PathConfig pathConfig, ServletContext servletContext) {
        this.pathConfig = pathConfig;
        this.servletContext = servletContext;
    }

    @GetMapping("/model/**")
    public ResponseEntity<Resource> getModelFile(HttpServletRequest request) {
        return getResourceFile(request, "/files/model/", pathConfig.getModelDir());
    }

    @GetMapping("/project/**")
    public ResponseEntity<Resource> getProjectFile(HttpServletRequest request) {
        return getResourceFile(request, "/files/project/", pathConfig.getProjectDir());
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

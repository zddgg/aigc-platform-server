package space.wenliang.ai.aigcplatformserver.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Configuration
public class PathConfig {

    @Value("${remote.enable:false}")
    private Boolean remoteEnable;
    @Value("${remote.platform}")
    private String remotePlatform;
    @Value("${remote.model-dir}")
    private String remoteModelDir;

    @Autowired
    private Environment env;

    private String modelDir;
    private String projectDir;

    private String modelUrl;
    private String projectUrl;

    @PostConstruct
    public void init() {
        String userDir = env.getProperty("user.dir");

        String host = "127.0.0.1";
        String port = env.getProperty("server.port");

        modelUrl = STR."http://\{host}:\{port}/files/model/";
        projectUrl = STR."http://\{host}:\{port}/files/project/";

        modelDir = env.getProperty("sc_model_dir");
        projectDir = env.getProperty("sc_project_dir");

        if (StringUtils.isBlank(modelDir)) {
            modelDir = userDir + File.separator + "model";
        }

        if (!remoteEnable) {
            remoteModelDir = userDir + File.separator + "model";
        }

        if (StringUtils.isBlank(projectDir)) {
            projectDir = userDir + File.separator + "project";
        }
    }

    public String hasRemotePlatForm() {
        return remoteEnable ? remotePlatform : "";
    }

    public String buildModelUrl(String[] dirs, String... path) {
        List<String> var = new ArrayList<>();
        var.addAll(Arrays.asList(dirs));
        var.addAll(Arrays.asList(path));
        return modelUrl + urlEncode(var);
    }

    public String buildModelUrl(String... path) {
        return modelUrl + urlEncode(Arrays.asList(path));
    }

    public String buildProjectUrl(String... path) {
        return projectUrl + urlEncode(Arrays.asList(path));
    }

    public static String urlEncode(List<String> path) {
        return path.stream()
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .map(s -> s.replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }
}

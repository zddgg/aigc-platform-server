package space.wenliang.ai.aigcplatformserver.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Configuration
@RequiredArgsConstructor
public class EnvConfig implements StartHook.StartHookListener {

    @Value("${server.domain:}")
    private String domain;

    @Value("${server.port:}")
    private String port;

    @Value("${user.dir:}")
    private String userDir;

    @Value("${application.version:}")
    private String applicationVersion;

    private String modelDir;
    private String configDir;
    private String projectDir;

    private String modelUrl;
    private String projectUrl;

    public static String urlEncode(List<String> path) {
        return path.stream()
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .map(s -> s.replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    @Override
    public void startHook() {

        if (StringUtils.isBlank(domain)) {
            String host = "127.0.0.1";
            domain = "http://" + host + ":" + port;
        }

        modelUrl = STR."\{domain}/files/model/";
        projectUrl = STR."\{domain}/files/project/";

        if (StringUtils.isBlank(modelDir)) {
            modelDir = userDir + File.separator + "model";
        }

        if (StringUtils.isBlank(configDir)) {
            configDir = userDir + File.separator + "config";
        }

        if (StringUtils.isBlank(projectDir)) {
            projectDir = userDir + File.separator + "project";
        }
    }

    public Path buildModelPath(String... path) {
        return Path.of(modelDir, path);
    }

    public Path buildConfigPath(String... names) {
        return Path.of(configDir, names);
    }

    public Path buildProjectPath(String... names) {
        return Path.of(projectDir, names);
    }

    public String buildModelUrl(String... path) {
        return modelUrl + urlEncode(Arrays.asList(path));
    }

    public String buildProjectUrl(String... path) {
        return projectUrl + urlEncode(Arrays.asList(path));
    }
}

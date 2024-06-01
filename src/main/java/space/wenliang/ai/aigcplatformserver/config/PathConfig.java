package space.wenliang.ai.aigcplatformserver.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

@Data
@Configuration
public class PathConfig {

    @Autowired
    private Environment env;

    private String scModelDir;
    private String scProjectDir;

    private String modelUrl;
    private String projectUrl;

    @PostConstruct
    public void init() {
        String userDir = env.getProperty("user.dir");
        modelUrl = STR."http://localhost:\{env.getProperty("server.port")}/model/";
        projectUrl = STR."http://localhost:\{env.getProperty("server.port")}/project/";

        scModelDir = env.getProperty("sc_model_dir");
        scProjectDir = env.getProperty("sc_project_dir");

        if (StringUtils.isBlank(scModelDir)) {
            scModelDir = userDir + File.separator + "model";
        }

        if (StringUtils.isBlank(scProjectDir)) {
            scProjectDir = userDir + File.separator + "project";
        }
    }

    public String buildModelUrl(String[] dirs, String... path) {
        return modelUrl + String.join("/", dirs) + "/" + String.join("/", path);
    }

    public String buildModelUrl(String... path) {
        return modelUrl + String.join("/", path);
    }

    public String buildProjectUrl(String... path) {
        return projectUrl + String.join("/", path);
    }
}

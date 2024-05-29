package space.wenliang.ai.aigcplatformserver.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

@Data
@Configuration
public class PathConfig {

    @Value("${user.dir}")
    private String userDir;

    @Autowired
    private Environment env;

    private String fsDir;
    private String fsUrl;

    @PostConstruct
    public void init() {
        fsDir = Path.of(userDir, "story-caster").toAbsolutePath().normalize().toString();
        fsUrl = STR."http://localhost:\{env.getProperty("server.port")}/files/";
    }

    public String buildFsUrl(String... path) {
        return fsUrl + String.join("/", path);
    }

    public String buildFsUrl(String[] dir, String... path) {
        return fsUrl + String.join("/", dir) + "/" + String.join("/", path);
    }
}

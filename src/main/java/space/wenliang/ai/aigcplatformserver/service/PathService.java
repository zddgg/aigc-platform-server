package space.wenliang.ai.aigcplatformserver.service;

import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;

import java.nio.file.Path;

@Service
public class PathService {

    private final PathConfig pathConfig;

    public PathService(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }


    public Path buildModelPath(String... names) {
        return Path.of(pathConfig.getScModelDir(), names);
    }

    public Path buildRmModelPath(String... names) {
        return Path.of(pathConfig.getRemoteModelDir(), names);
    }

    public Path buildProjectPath(String... names) {
        return Path.of(pathConfig.getScProjectDir(), names);
    }

    public Path getCommonRolesPath(String project) {
        return buildProjectPath("text", project, "config", "common-roles.json");
    }

    public Path getRolesPath(String project, String chapter) {
        return buildProjectPath("text", project, "章节", chapter, "roles.json");
    }

    public Path getChapterInfoPath(String project, String chapter) {
        return buildProjectPath("text", project, "章节", chapter, "chapterInfo.json");
    }

    public Path getAiResultPath(String project, String chapter) {
        return buildProjectPath("text", project, "章节", chapter, "aiResult.json");
    }

    public Path getEdgeTtsConfigPath() {
        return buildProjectPath("config", "edge-tts-config.json");
    }

    public Path getAudioServerConfigPath() {
        return buildProjectPath("config", "audio-server-config.json");
    }

    public Path getChatConfigPath() {
        return buildProjectPath("config", "chat-config.json");
    }

    public Path getRefAudioConfigPath() {
        return buildProjectPath("config", "ref-audio-config.json");
    }
}

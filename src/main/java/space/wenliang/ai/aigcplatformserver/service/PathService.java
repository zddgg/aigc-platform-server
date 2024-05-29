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

    public Path getCommonRolesPath(String project) {
        return Path.of(pathConfig.getFsDir(), "text", project, "config", "common-roles.json");
    }

    public Path getRolesPath(String project, String chapter) {
        return Path.of(pathConfig.getFsDir(), "text", project, "章节", chapter, "roles.json");
    }

    public Path getChapterInfoPath(String project, String chapter) {
        return Path.of(pathConfig.getFsDir(), "text", project, "章节", chapter, "chapterInfo.json");
    }

    public Path getAiResultPath(String project, String chapter) {
        return Path.of(pathConfig.getFsDir(), "text", project, "章节", chapter, "aiResult.json");
    }

    public Path getEdgeTtsConfigPath() {
        return Path.of(pathConfig.getFsDir(), "config", "edge-tts-config.json");
    }

    public Path getAudioServerConfigPath() {
        return Path.of(pathConfig.getFsDir(), "config", "audio-server-config.json");
    }

    public Path getChatConfigPath() {
        return Path.of(pathConfig.getFsDir(), "config", "chat-config.json");
    }

    public Path getRefAudioConfigPath() {
        return Path.of(pathConfig.getFsDir(), "config", "ref-audio-config.json");
    }
}

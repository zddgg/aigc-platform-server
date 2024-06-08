package space.wenliang.ai.aigcplatformserver.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.model.*;
import space.wenliang.ai.aigcplatformserver.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ConfigService {

    private final PathService pathService;

    public ConfigService(PathService pathService) {
        this.pathService = pathService;
    }

    public EdgeTtsConfig getEdgeTtsConfig() throws Exception {
        Path path = pathService.getEdgeTtsConfigPath();
        return FileUtils.getObjectFromFile(path, EdgeTtsConfig.class);
    }

    public void saveEdgeTtsConfig(EdgeTtsConfig config) throws Exception {
        Path path = pathService.getEdgeTtsConfigPath();
        Files.write(path, JSON.toJSONBytes(config));
    }

    public List<AudioServerConfig> getAudioServerConfigs() throws Exception {
        Path path = pathService.getAudioServerConfigPath();
        return FileUtils.getListFromFile(path, AudioServerConfig.class);
    }

    public void saveAudioServerConfigs(List<AudioServerConfig> configs) throws Exception {
        Path path = pathService.getAudioServerConfigPath();
        Files.write(path, JSON.toJSONBytes(configs));
    }

    public ChatConfig getChatConfig() {
        Path path = pathService.getChatConfigPath();
        return FileUtils.getObjectFromFile(path, ChatConfig.class);
    }

    public void saveChatConfig(ChatConfig config) throws Exception {
        Path path = pathService.getChatConfigPath();
        Files.write(path, JSON.toJSONBytes(config));
    }

    public List<RefAudio> getRefAudioConfig() throws Exception {
        Path path = pathService.getRefAudioConfigPath();
        return FileUtils.getListFromFile(path, RefAudio.class);
    }

    public void saveRefAudioConfig(List<RefAudio> config) throws Exception {
        Path path = pathService.getRefAudioConfigPath();
        Files.write(path, JSON.toJSONBytes(config));
    }

    public List<RefAudioSort> getRefAudioSort() throws Exception {
        Path path = pathService.getRefAudioSortPath();
        return FileUtils.getListFromFile(path, RefAudioSort.class);
    }

    public void saveRefAudioSort(List<RefAudioSort> refAudioSorts) throws Exception {
        Path path = pathService.getRefAudioSortPath();
        Files.write(path, JSON.toJSONBytes(refAudioSorts));
    }

    public List<ChatTtsConfig> getChatTtsConfig() throws Exception {
        Path path = pathService.getChatTtsConfigPath();
        return FileUtils.getListFromFile(path, ChatTtsConfig.class);
    }

    public void saveChatTtsConfig(List<ChatTtsConfig> config) throws Exception {
        Path path = pathService.getChatTtsConfigPath();
        Files.write(path, JSON.toJSONBytes(config));
    }
}

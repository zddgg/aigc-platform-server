package space.wenliang.ai.aigcplatformserver.model.audio;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.model.AudioServerConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AudioCreater {

    private final ConfigService configService;

    private final Map<String, IAudioCreater> audioCreaterMap;

    public AudioCreater(ConfigService configService, Map<String, IAudioCreater> audioCreaterMap) {
        this.configService = configService;
        this.audioCreaterMap = audioCreaterMap;
    }

    public ResponseEntity<byte[]> createAudio(AudioContext context) throws Exception {
        List<AudioServerConfig> audioServerConfigs = configService.getAudioServerConfigs();
        Map<String, AudioServerConfig> audioServerMap = audioServerConfigs.stream()
                .collect(Collectors.toMap(AudioServerConfig::getName, Function.identity()));
        context.setAudioServerConfig(audioServerMap.get(context.getType()));

        if (audioCreaterMap.containsKey(context.getType())) {
            return audioCreaterMap.get(context.getType()).createAudio(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }

    public void createFile(AudioContext context) throws Exception {
        List<AudioServerConfig> audioServerConfigs = configService.getAudioServerConfigs();
        Map<String, AudioServerConfig> audioServerMap = audioServerConfigs.stream()
                .collect(Collectors.toMap(AudioServerConfig::getName, Function.identity()));
        context.setAudioServerConfig(audioServerMap.get(context.getType()));

        if (audioCreaterMap.containsKey(context.getType())) {
            audioCreaterMap.get(context.getType()).createFile(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }
}

package space.wenliang.ai.aigcplatformserver.controller.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.AudioServerConfig;
import space.wenliang.ai.aigcplatformserver.bean.model.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.bean.model.RefAudio;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioCreater;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;
import space.wenliang.ai.aigcplatformserver.service.ModelService;
import space.wenliang.ai.aigcplatformserver.service.PathService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("model/edge-tts")
public class EdgeTtsController {

    private final PathConfig pathConfig;
    private final PathService pathService;
    private final AudioCreater audioCreater;
    private final ConfigService configService;
    private final ModelService modelService;

    public EdgeTtsController(PathConfig pathConfig, PathService pathService, AudioCreater audioCreater, ConfigService configService, ModelService modelService) {
        this.pathConfig = pathConfig;
        this.pathService = pathService;
        this.audioCreater = audioCreater;
        this.configService = configService;
        this.modelService = modelService;
    }

    @PostMapping("queryVoices")
    public Result<Object> queryVoices() throws Exception {

        List<EdgeTtsVoice> voices = configService.getEdgeTtsConfig().getVoices()
                .stream()
                .filter(edgeTtsVoice -> edgeTtsVoice.getShortName().startsWith("zh")
                        || edgeTtsVoice.getShortName().startsWith("en")
                        || edgeTtsVoice.getShortName().startsWith("ja")
                        || edgeTtsVoice.getShortName().startsWith("ko")
                )
                .toList();

        Map<String, String> etAudiomap = modelService.getAudios().stream()
                .filter(refAudio -> StringUtils.equals(refAudio.getGroup(), "edge-tts"))
                .collect(Collectors.toMap(RefAudio::getName, v -> v.getMoods().getFirst().getMoodAudios().getFirst().getUrl()));

        for (EdgeTtsVoice voice : voices) {
            if (etAudiomap.containsKey(voice.getShortName())) {
                voice.setUrl(etAudiomap.get(voice.getShortName()));
            }
        }

        return Result.success(voices);
    }

    @PostMapping("playAudio")
    public Result<Object> playAudio(@SingleValueParam("voice") String voice) throws Exception {

        Path voiceAudioDir = pathService.buildModelPath("ref-audio", "edge-tts", voice);

        AtomicReference<String> audioName = new AtomicReference<>("");
        if (Files.exists(voiceAudioDir)) {
            Files.list(voiceAudioDir).forEach(path -> {
                if (Files.isDirectory(path)) {
                    try {
                        Optional<Path> first = Files.list(path).findFirst();
                        first.ifPresent(value -> audioName.set(path.getFileName().toString() + "/" + value.getFileName().toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        if (StringUtils.isNotBlank(audioName.get())) {
            String audio = pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, audioName.get());
            return Result.success(audio);
        }

        List<AudioServerConfig> audioServerConfigs = configService.getAudioServerConfigs();
        Map<String, String> audioServerMap = audioServerConfigs.stream()
                .collect(Collectors.toMap(AudioServerConfig::getName, AudioServerConfig::getServerUrl));

        AudioContext audioContext = new AudioContext();

        audioContext.setType("edge-tts");
        audioContext.setUrl(audioServerMap.get("edge-tts"));

        String text = "你好呀。今天，也是充满希望的一天！";
        audioContext.setText(text);
        audioContext.setOutputDir(Path.of(voiceAudioDir.toAbsolutePath().toString(), "默认").toAbsolutePath().toString());
        audioContext.setOutputName(text);
        audioContext.setSpeaker(voice);
        audioCreater.createFile(audioContext);
        String audio = pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, "默认", FileUtils.fileNameFormat(text + ".wav"));
        return Result.success(audio);
    }
}

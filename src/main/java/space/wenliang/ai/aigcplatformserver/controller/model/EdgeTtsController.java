package space.wenliang.ai.aigcplatformserver.controller.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.EdgeTtsConfig;
import space.wenliang.ai.aigcplatformserver.bean.model.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("model/edge-tts")
public class EdgeTtsController {

    private final PathConfig pathConfig;
    private final PathService pathService;
    private final AudioCreater audioCreater;
    private final ConfigService configService;

    public EdgeTtsController(PathConfig pathConfig, PathService pathService, AudioCreater audioCreater, ConfigService configService, ModelService modelService) {
        this.pathConfig = pathConfig;
        this.pathService = pathService;
        this.audioCreater = audioCreater;
        this.configService = configService;
    }

    @PostMapping("playAudio")
    public Result<Object> playAudio(@SingleValueParam("voice") String voice) throws Exception {

        Path voiceAudioDir = pathService.buildModelPath("ref-audio", "edge-tts", voice);

        AtomicReference<String> moodName = new AtomicReference<>("");
        AtomicReference<String> audioName = new AtomicReference<>("");
        if (Files.exists(voiceAudioDir)) {
            Files.list(voiceAudioDir).forEach(path -> {
                if (Files.isDirectory(path)) {
                    try {
                        Optional<Path> first = Files.list(path).findFirst();
                        first.ifPresent(value -> {
                            moodName.set(path.getFileName().toString());
                            audioName.set(value.getFileName().toString());
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        if (StringUtils.isNotBlank(audioName.get())) {
            String audio = pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, moodName.get(), audioName.get());
            return Result.success(audio);
        }

        EdgeTtsConfig edgeTtsConfig = configService.getEdgeTtsConfig();
        List<EdgeTtsVoice> voices = edgeTtsConfig.getVoices();
        List<EdgeTtsConfig.LangText> langTexts = edgeTtsConfig.getLangTexts();
        Optional<EdgeTtsVoice> voiceOptional = voices.stream().filter(v -> StringUtils.equals(v.getShortName(), voice)).findFirst();
        if (voiceOptional.isEmpty()) {
            throw new BizException("edge-tts speaker [" + voice + "] not found");
        }
        Optional<EdgeTtsConfig.LangText> langTextOptional = langTexts.stream()
                .filter(f -> StringUtils.equals(f.getEnName(),
                        voiceOptional.get().getLocale().substring(0, voiceOptional.get().getLocale().indexOf("-"))))
                .findFirst();
        if (langTextOptional.isEmpty() || StringUtils.isBlank(langTextOptional.get().getText())) {
            throw new BizException("edge-tts speak text not configured");
        }

        AudioContext audioContext = new AudioContext();

        audioContext.setType("edge-tts");

        String text = langTextOptional.get().getText();
        audioContext.setText(text);
        audioContext.setOutputDir(Path.of(voiceAudioDir.toAbsolutePath().toString(), "默认").toAbsolutePath().toString());
        audioContext.setOutputName(text);
        audioContext.setSpeaker(voice);
        audioCreater.createFile(audioContext);
        String audio = pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, "默认", FileUtils.fileNameFormat(text + ".wav"));
        return Result.success(audio);
    }
}

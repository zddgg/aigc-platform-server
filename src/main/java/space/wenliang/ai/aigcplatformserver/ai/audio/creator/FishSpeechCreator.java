package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.util.PathWrapperUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("fish-speech")
public class FishSpeechCreator extends AbsAudioCreator {

    private final PathConfig pathConfig;

    public FishSpeechCreator(RestClient restClient, PathConfig pathConfig) {
        super(restClient);
        this.pathConfig = pathConfig;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        RefAudioEntity refAudio = context.getRefAudio();
        FishSpeechConfigEntity config = context.getFishSpeechConfig();

        Path audioPath = pathConfig.buildRmModelPath(
                "ref-audio", refAudio.getAudioGroup(), refAudio.getAudioName(), refAudio.getMoodName(), refAudio.getMoodAudioName());

        String refAudioPath = PathWrapperUtils.getAbsolutePath(audioPath, pathConfig.hasRemotePlatForm());

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());
        params.put("reference_audio_path", refAudioPath);
        params.put("reference_text", refAudio.getMoodAudioText());

        params.put("top_p", config.getTopP());
        params.put("temperature", config.getTemperature());
        params.put("repetition_penalty", config.getRepetitionPenalty());


        return params;
    }
}

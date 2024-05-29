package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("fish-speech")
public class FishSpeechCreater extends AbsAudioCreater {

    public FishSpeechCreater(RestClient restClient) {
        super(restClient);
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());

        params.put("reference_audio_path", context.getRefAudioPath());
        params.put("reference_text", context.getRefText());

        return params;
    }

    @Override
    public void format(AudioContext context) {

    }
}

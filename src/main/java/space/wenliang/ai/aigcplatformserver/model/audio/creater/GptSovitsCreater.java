package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("gpt-sovits")
public class GptSovitsCreater extends AbsAudioCreater {

    public GptSovitsCreater(RestClient restClient) {
        super(restClient);
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());
        params.put("text_lang", context.getTextLanguage());

        params.put("ref_audio_path", context.getRefAudioPath());
        params.put("prompt_text", context.getRefText());
        params.put("prompt_lang", context.getRefTextLanguage());
        return params;
    }

    @Override
    public void format(AudioContext context) {

    }
}

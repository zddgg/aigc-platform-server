package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatTtsConfig;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;

import java.util.HashMap;
import java.util.Map;

@Service("chat-tts")
public class ChatTtsCreater extends AbsAudioCreater {

    public ChatTtsCreater(RestClient restClient) {
        super(restClient);
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        ChatTtsConfig chatTtsConfig = context.getChatTtsConfig();
        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());
        params.put("temperature", chatTtsConfig.getTemperature());
        params.put("top_P", chatTtsConfig.getTop_P());
        params.put("top_K", chatTtsConfig.getTop_K());
        params.put("audio_seed_input", chatTtsConfig.getAudio_seed_input());
        params.put("text_seed_input", chatTtsConfig.getText_seed_input());
        params.put("refine_text_flag", chatTtsConfig.getRefine_text_flag());
        params.put("params_refine_text", chatTtsConfig.getParams_refine_text());

        return params;
    }
}

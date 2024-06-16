package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;

import java.util.HashMap;
import java.util.Map;

@Service("chat-tts")
public class ChatTtsCreator extends AbsAudioCreator {

    public ChatTtsCreator(RestClient restClient) {
        super(restClient);
    }


    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        ChatTtsConfigEntity chatTtsConfig = context.getChatTtsConfig();
        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());
        params.put("temperature", chatTtsConfig.getTemperature());
        params.put("top_P", chatTtsConfig.getTopP());
        params.put("top_K", chatTtsConfig.getTopK());
        params.put("audio_seed_input", chatTtsConfig.getAudioSeedInput());
        params.put("text_seed_input", chatTtsConfig.getTextSeedInput());
        params.put("refine_text_flag", chatTtsConfig.getRefineTextFlag());
        params.put("params_refine_text", chatTtsConfig.getRefineTextParams());

        return params;
    }
}

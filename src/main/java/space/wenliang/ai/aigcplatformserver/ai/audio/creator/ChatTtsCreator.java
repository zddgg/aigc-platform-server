package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service("chat-tts")
public class ChatTtsCreator extends AbsAudioCreator {

    public ChatTtsCreator(RestClient restClient) {
        super(restClient);
    }


    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        ChatTtsConfigEntity config = context.getChatTtsConfig();
        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());

        if (Objects.nonNull(config)) {
            if (Objects.nonNull(config.getTemperature())) {
                params.put("temperature", config.getTemperature());
            }
            if (Objects.nonNull(config.getTopP())) {
                params.put("top_P", config.getTopP());
            }
            if (Objects.nonNull(config.getTopK())) {
                params.put("top_K", config.getTopK());
            }
            if (Objects.nonNull(config.getAudioSeedInput())) {
                params.put("audio_seed_input", config.getAudioSeedInput());
            }
            if (Objects.nonNull(config.getTextSeedInput())) {
                params.put("text_seed_input", config.getTextSeedInput());
            }
            if (Objects.nonNull(config.getRefineTextFlag())) {
                params.put("refine_text_flag", config.getRefineTextFlag());
            }
            if (Objects.nonNull(config.getRefineTextParams())) {
                params.put("params_refine_text", config.getRefineTextParams());
            }
        }

        return params;
    }
}

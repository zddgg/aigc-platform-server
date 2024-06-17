package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;

import java.util.HashMap;
import java.util.Map;

@Service("edge-tts")
public class EdgeTtsCreater extends AbsAudioCreater {

    public EdgeTtsCreater(RestClient restClient) {
        super(restClient);
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("mp3");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());

        params.put("voice", context.getSpeaker());

        return params;
    }
}

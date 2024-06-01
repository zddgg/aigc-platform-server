package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.utils.AudioUtils;

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Override
    public void format(AudioContext context) throws Exception {
        String input = Path.of(context.getOutputDir(), context.getOutputName() + "." + context.getMediaType()).toString();
        String output = Path.of(context.getOutputDir(), context.getOutputName() + ".wav").toString();
        AudioUtils.mp3ToWav(input, output);
        Files.deleteIfExists(Path.of(input));
    }
}

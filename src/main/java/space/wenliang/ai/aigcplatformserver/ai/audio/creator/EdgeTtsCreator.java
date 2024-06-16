package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.bean.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.entity.AudioServerConfigEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.AAudioServerConfigService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("edge-tts")
public class EdgeTtsCreator extends AbsAudioCreator {

    private final AAudioServerConfigService aAudioServerConfigService;

    public EdgeTtsCreator(RestClient restClient,
                          AAudioServerConfigService aAudioServerConfigService) {
        super(restClient);
        this.aAudioServerConfigService = aAudioServerConfigService;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getText());

        params.put("voice", context.getEdgeTtsConfig().getShortName());

        return params;
    }

    public List<EdgeTtsVoice> getEdgeTtsVoices() {
        Optional<AudioServerConfigEntity> optional = aAudioServerConfigService.list()
                .stream()
                .filter(a -> StringUtils.equals(a.getName(), "edge-tts"))
                .findAny();

        if (optional.isEmpty()) {
            throw new BizException("没有找到edge-tts音频服务配置");
        }

        String host = optional.get().getHost();

        String body = super.restClient
                .post()
                .uri(host + "/voices")
                .retrieve()
                .body(String.class);
        System.out.println(body);
        return JSON.parseArray(body, EdgeTtsVoice.class);
    }
}

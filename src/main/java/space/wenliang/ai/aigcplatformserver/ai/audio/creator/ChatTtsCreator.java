package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("chat-tts")
public class ChatTtsCreator extends AbsAudioCreator {

    public ChatTtsCreator(RestClient restClient,
                          PinyinCacheService pinyinCacheService) {
        super(restClient, pinyinCacheService);
    }


    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getMarkupText());

        JSONObject config = JSON.parseObject(context.getAmMcParamsJson());

        if (Objects.nonNull(config)) {
            Map<String, Object> filterConfig = config.entrySet().stream()
                    .filter((e) -> e.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            params.putAll(filterConfig);
        }
        return params;
    }
}

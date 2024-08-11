package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.bean.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.entity.AmServerEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.AmServerService;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("edge-tts")
public class EdgeTtsCreator extends AbsAudioCreator {

    private final AmServerService amServerService;

    public EdgeTtsCreator(RestClient restClient,
                          AmServerService amServerService,
                          PinyinCacheService pinyinCacheService) {
        super(restClient, pinyinCacheService);
        this.amServerService = amServerService;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getMarkupText());

        params.put("voice", JSON.parseObject(context.getAmMcParamsJson()).getString("shortName"));

        return params;
    }

    public List<EdgeTtsVoice> getVoices() {
        AmServerEntity amServer = amServerService.getOne(new LambdaQueryWrapper<AmServerEntity>()
                .eq(AmServerEntity::getName, ModelTypeEnum.edge_tts.getName()));

        if (Objects.isNull(amServer)) {
            throw new BizException("没有找到edge-tts音频服务配置");
        }

        String body = super.restClient
                .post()
                .uri(amServer.getHost() + "/voices")
                .retrieve()
                .body(String.class);
        return JSON.parseArray(body, EdgeTtsVoice.class);
    }
}

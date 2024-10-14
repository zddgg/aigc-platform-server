package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static space.wenliang.ai.aigcplatformserver.common.CommonConstants.prompt_audio;

@Slf4j
@Service("cosy-voice")
public class CosyVoiceCreator extends AbsAudioCreator {

    private final EnvConfig envConfig;

    public CosyVoiceCreator(EnvConfig envConfig,
                            RestClient restClient,
                            PinyinCacheService pinyinCacheService) {
        super(restClient, pinyinCacheService);
        this.envConfig = envConfig;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");
        return Map.of();
    }

    @Override
    public ResponseEntity<byte[]> creator(AudioContext context) {

        super.textMarkup(context);

        JSONObject config = JSON.parseObject(context.getAmMcParamsJson());

        String mode = config.getString("mode");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("tts_text", context.getMarkupText());
        builder.part("spk_id", config.getString("role"));
        builder.part("seed", Optional.ofNullable(config.getInteger("seed")).orElse(0));

        String path = "";
        if (StringUtils.equals(mode, "preset")) {
            path = "/inference_sft";
        }

        if (StringUtils.equals(mode, "custom")) {
            path = "/inference_zero_shot";

            Path audioPath = envConfig.buildModelPath(
                    prompt_audio, context.getAmPaGroup(), context.getAmPaRole(), context.getAmPaMood(), context.getAmPaAudio());
            builder.part("prompt_text", context.getAmPaAudioText());
            builder.part("prompt_wav", new FileSystemResource(audioPath));
        }

        if (StringUtils.equals(mode, "advanced")) {
            path = "/inference_instruct";

            builder.part("instruct_text", config.getString("instruct"));
        }

        return restClient
                .post()
                .uri(context.getAmServer().getHost() + path)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .toEntity(byte[].class);
    }
}

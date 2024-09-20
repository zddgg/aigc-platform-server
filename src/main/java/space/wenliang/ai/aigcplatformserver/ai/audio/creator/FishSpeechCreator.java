package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static space.wenliang.ai.aigcplatformserver.common.CommonConstants.prompt_audio;

@Slf4j
@Service("fish-speech")
public class FishSpeechCreator extends AbsAudioCreator {

    private final EnvConfig envConfig;

    private final WebClient msgpackWebClient;

    public FishSpeechCreator(EnvConfig envConfig,
                             RestClient restClient,
                             PinyinCacheService pinyinCacheService,
                             WebClient msgpackWebClient) {
        super(restClient, pinyinCacheService);
        this.envConfig = envConfig;
        this.msgpackWebClient = msgpackWebClient;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Path audioPath = envConfig.buildModelPath(
                prompt_audio, context.getAmPaGroup(), context.getAmPaRole(), context.getAmPaMood(), context.getAmPaAudio());

        Map<String, Object> params = new HashMap<>();

        params.put("text", context.getMarkupText());

        try {
            params.put("references", List.of(Map.of(
                    "audio", Files.readAllBytes(audioPath),
                    "text", context.getAmPaAudioText()
            )));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        params.put("format", "wav");

        JSONObject config = JSON.parseObject(context.getAmMcParamsJson());

        if (Objects.nonNull(config) && !StringUtils.equals(context.getAmMcId(), "-1")) {

            Map<String, Object> filterConfig = config.entrySet().stream()
                    .filter((e) -> e.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            params.putAll(filterConfig);
        }

        return params;
    }

    public ResponseEntity<byte[]> creator(AudioContext context) {

        textMarkup(context);

        Map<String, Object> params = buildParams(context);

        return msgpackWebClient
                .post()
                .uri(context.getAmServer().getHost() + context.getAmServer().getPath())
                .contentType(MediaType.parseMediaType("application/msgpack"))
                .bodyValue(params)
                .retrieve()
                .toEntity(byte[].class).block();
    }
}

package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static space.wenliang.ai.aigcplatformserver.common.CommonConstants.prompt_audio;

@Slf4j
@Service("gpt-sovits")
public class GptSovitsCreator extends AbsAudioCreator {

    private final EnvConfig envConfig;

    public GptSovitsCreator(EnvConfig envConfig,
                            RestClient restClient,
                            PinyinCacheService pinyinCacheService) {
        super(restClient, pinyinCacheService);
        this.envConfig = envConfig;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Path audioPath = envConfig.buildModelPath(
                prompt_audio, context.getAmPaGroup(), context.getAmPaRole(), context.getAmPaMood(), context.getAmPaAudio());

        String refAudioPathStr = audioPath.toAbsolutePath().toString();

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.equalsIgnoreCase(context.getAmServer().getApiVersion(), "v1")) {
            params.put("text", context.getMarkupText());
            params.put("text_language", Optional.ofNullable(context.getTextLang()).orElse("zh"));

            params.put("refer_wav_path", refAudioPathStr);
            params.put("prompt_text", context.getAmPaAudioText());
            params.put("prompt_language", Optional.ofNullable(context.getAmPaAudioLang()).orElse("zh"));

        }

        if (StringUtils.equalsIgnoreCase(context.getAmServer().getApiVersion(), "v2")) {

            params.put("text", context.getMarkupText());
            params.put("text_language", Optional.ofNullable(context.getTextLang()).orElse("zh"));

            params.put("refer_wav_path", refAudioPathStr);
            params.put("prompt_text", context.getAmPaAudioText());
            params.put("prompt_language", Optional.ofNullable(context.getAmPaAudioLang()).orElse("zh"));

            JSONObject config = JSON.parseObject(context.getAmMcParamsJson());

            if (Objects.nonNull(config) && !StringUtils.equals(context.getAmMcId(), "-1")) {

                Map<String, Object> filterConfig = config.entrySet().stream()
                        .filter((e) -> e.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                params.putAll(filterConfig);
            }
        }

        return params;
    }

    @Override
    public void pre(AudioContext context) {

        if (StringUtils.equalsIgnoreCase(context.getAmServer().getApiVersion(), "v2")) {
            try {

                String host = context.getAmServer().getHost();

                String gpt_weights = null;
                String sovits_weights = null;
                for (JSONObject jsonObject : JSON.parseArray(context.getAmMfJson(), JSONObject.class)) {
                    String fileType = jsonObject.getString("fileType");
                    String fileName = jsonObject.getString("fileName");
                    if (StringUtils.equals(fileType, "ckpt")) {
                        gpt_weights = envConfig.buildModelPath("gpt-sovits", context.getAmMfGroup(), context.getAmMfRole(), fileName).toAbsolutePath().toString();
                    }
                    if (StringUtils.equals(fileType, "pth")) {
                        sovits_weights = envConfig.buildModelPath("gpt-sovits", context.getAmMfGroup(), context.getAmMfRole(), fileName).toAbsolutePath().toString();
                    }
                }

                if (StringUtils.isNotBlank(gpt_weights) && StringUtils.isNotBlank(sovits_weights)) {
                    Map<String, String> params = Map.of("gpt_model_path", gpt_weights, "sovits_model_path", sovits_weights);

                    log.info("开始切换GPT-SoVITS模型, {}", gpt_weights);

                    ResponseEntity<String> response = restClient
                            .post()
                            .uri(host + "/set_model")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(params)
                            .retrieve()
                            .toEntity(String.class);

                    log.info("切换GPT-SoVITS模型成功, {}", response.getBody());
                }
            } catch (ResourceAccessException e) {
                log.error("切换GPT-SoVITS模型失败", e);
                throw new BizException("音频生成服务连接异常，服务类型：" + context.getAmType());
            } catch (Exception e) {
                log.error("切换GPT-SoVITS模型失败", e);
                throw new BizException("切换GPT-SoVITS模型失败");
            }
        }
    }
}

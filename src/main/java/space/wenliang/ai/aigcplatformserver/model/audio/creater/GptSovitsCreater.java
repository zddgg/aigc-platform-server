package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.service.PathService;
import space.wenliang.ai.aigcplatformserver.utils.PathWrapper;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service("gpt-sovits")
public class GptSovitsCreater extends AbsAudioCreater {

    private final PathConfig pathConfig;

    private final PathService pathService;

    private final RestTemplate restTemplate;

    public GptSovitsCreater(PathService pathService, RestClient restClient, PathConfig pathConfig, RestTemplate restTemplate) {
        super(restClient);
        this.pathService = pathService;
        this.pathConfig = pathConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v1")) {
            params.put("text", context.getText());
            params.put("text_language", context.getTextLang());

            params.put("refer_wav_path", context.getRefAudioPath());
            params.put("prompt_text", context.getRefText());
            params.put("prompt_language", context.getRefTextLang());

        }

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v2")) {
            params.put("text", context.getText());
            params.put("text_lang", context.getTextLang());

            params.put("ref_audio_path", context.getRefAudioPath());
            params.put("prompt_text", context.getRefText());
            params.put("prompt_lang", context.getRefTextLang());
        }

        return params;
    }

    @Override
    public void pre(AudioContext context) {

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v2")) {
            Map<String, Object> params = new HashMap<>();

            params.put("text", context.getText());
            params.put("text_lang", context.getTextLang());

            params.put("ref_audio_path", context.getRefAudioPath());
            params.put("prompt_text", context.getRefText());
            params.put("prompt_lang", context.getRefTextLang());

            try {

                URI uri = new URI(context.getAudioServerConfig().getServerUrl());

                Path modelPath = pathService.buildModelPath("gpt-sovits", context.getModelGroup(), context.getModel());
                AtomicReference<String> gpt_weights = new AtomicReference<>("");
                AtomicReference<String> sovits_weights = new AtomicReference<>("");
                if (Files.exists(modelPath) && Files.isDirectory(modelPath)) {
                    Files.list(modelPath).forEach(path -> {
                        Path rmModelPath = pathService.buildRmModelPath("gpt-sovits",
                                context.getModelGroup(), context.getModel(), path.getFileName().toString());

                        String wPath = PathWrapper.getAbsolutePath(rmModelPath, pathConfig.hasRemotePlatForm());
                        if (path.getFileName().toString().endsWith("ckpt")) {
                            gpt_weights.set(wPath);
                        }
                        if (path.getFileName().toString().endsWith("pth")) {
                            sovits_weights.set(wPath);
                        }
                    });
                }

                log.info("开始切换GptWeights模型");
                restTemplate.getForEntity(
                        uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort()
                                + "/set_gpt_weights?weights_path=" + gpt_weights.get(), String.class);

                restTemplate.getForEntity(
                        uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort()
                                + "/set_sovits_weights?weights_path=" + sovits_weights.get(), String.class
                );
                log.info("切换GptWeights模型成功");
            } catch (Exception e) {
                log.error("切换GptWeights模型失败", e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}

package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsModelEntity;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.util.PathWrapperUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service("gpt-sovits")
public class GptSovitsCreator extends AbsAudioCreator {

    private final PathConfig pathConfig;

    private final RestTemplate restTemplate;

    public GptSovitsCreator(RestClient restClient, PathConfig pathConfig, RestTemplate restTemplate) {
        super(restClient);
        this.pathConfig = pathConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> buildParams(AudioContext context) {
        context.setMediaType("wav");

        RefAudioEntity refAudio = context.getRefAudio();
        GptSovitsConfigEntity config = context.getGptSovitsConfig();

        Path audioPath = pathConfig.buildRmModelPath(
                "ref-audio", refAudio.getAudioGroup(), refAudio.getAudioName(), refAudio.getMoodName(), refAudio.getMoodAudioName());

        String refAudioPath = PathWrapperUtils.getAbsolutePath(audioPath, pathConfig.hasRemotePlatForm());


        Map<String, Object> params = new HashMap<>();

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v1")) {
            params.put("text", context.getText());
            params.put("text_language", Optional.ofNullable(context.getTextLang()).orElse("zh"));

            params.put("refer_wav_path", refAudioPath);
            params.put("prompt_text", refAudio.getMoodAudioText());
            params.put("prompt_language", Optional.ofNullable(refAudio.getLanguage()).orElse("zh"));

        }

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v2")) {

            params.put("text", context.getText());
            params.put("text_lang", Optional.ofNullable(context.getTextLang()).orElse("zh"));

            params.put("ref_audio_path", refAudioPath);
            params.put("prompt_text", refAudio.getMoodAudioText());
            params.put("prompt_lang", Optional.ofNullable(refAudio.getLanguage()).orElse("zh"));
            params.put("top_k", config.getTopK());
            params.put("top_p", config.getTopP());
            params.put("temperature", config.getTemperature());
            params.put("text_split_method", config.getTextSplitMethod());
            params.put("split_bucket", config.getSplitBucket());
            params.put("speed_factor", config.getSpeedFactor());
            params.put("fragment_interval", config.getFragmentInterval());
            params.put("seed", config.getSeed());
            params.put("media_type", "wav");
            params.put("parallel_infer", config.getParallelInfer());
            params.put("repetition_penalty", config.getRepetitionPenalty());
        }

        return params;
    }

    @Override
    public void pre(AudioContext context) {

        if (StringUtils.equalsIgnoreCase(context.getAudioServerConfig().getApiVersion(), "v2")) {

            GptSovitsModelEntity model = context.getGptSovitsModel();

            try {

                String host = context.getAudioServerConfig().getHost();

                Path gModelPath = pathConfig.buildModelPath("gpt-sovits", model.getModelGroup(), model.getModelName(), model.getCkpt());
                String gpt_weights = PathWrapperUtils.getAbsolutePath(gModelPath, pathConfig.hasRemotePlatForm());

                Path sModelPath = pathConfig.buildModelPath("gpt-sovits", model.getModelGroup(), model.getModelName(), model.getPth());
                String sovits_weights = PathWrapperUtils.getAbsolutePath(sModelPath, pathConfig.hasRemotePlatForm());

                log.info("开始切换GptWeights模型, {}", gpt_weights);
                restTemplate.getForEntity(
                        host + "/set_gpt_weights?weights_path=" + gpt_weights, String.class);

                log.info("开始切换SovitsWeights模型, {}", sovits_weights);
                restTemplate.getForEntity(
                        host + "/set_sovits_weights?weights_path=" + sovits_weights, String.class
                );
                log.info("切换模型成功");
            } catch (ResourceAccessException e) {
                log.error("切换GptWeights模型失败", e);
                throw new BizException("音频生成服务连接异常，服务类型：" + context.getType());
            } catch (Exception e) {
                log.error("切换GptWeights模型失败", e);
                throw new BizException("create audio failed");
            }
        }
    }
}

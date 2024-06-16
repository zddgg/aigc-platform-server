package space.wenliang.ai.aigcplatformserver.controller;

import cn.hutool.core.codec.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsModelEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.business.BGptSovitsConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BGptSovitsModelService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("gptSovits")
public class GptSovitsController {

    private final PathConfig pathConfig;
    private final AudioCreator audioCreator;
    private final BGptSovitsModelService bGptSovitsModelService;
    private final BGptSovitsConfigService bGptSovitsConfigService;

    public GptSovitsController(PathConfig pathConfig,
                               AudioCreator audioCreator,
                               BGptSovitsModelService bGptSovitsModelService,
                               BGptSovitsConfigService bGptSovitsConfigService) {
        this.pathConfig = pathConfig;
        this.audioCreator = audioCreator;
        this.bGptSovitsModelService = bGptSovitsModelService;
        this.bGptSovitsConfigService = bGptSovitsConfigService;
    }

    @PostMapping("modelList")
    public Result<Object> modelList() {
        List<GptSovitsModelEntity> models = bGptSovitsModelService.list();
        return Result.success(models);
    }

    @PostMapping("refreshCache")
    public Result<Object> refreshCache() {
        bGptSovitsModelService.refreshCache();
        return Result.success();
    }

    @PostMapping("configs")
    public Result<Object> configs() {
        return Result.success(bGptSovitsConfigService.configs());
    }

    @PostMapping("deleteConfig")
    public Result<Object> deleteConfig(@RequestBody GptSovitsConfigEntity config) {
        bGptSovitsConfigService.deleteConfig(config);
        return Result.success();
    }

    @PostMapping("playAudio")
    public ResponseEntity<byte[]> playAudio(@RequestBody GptSovitsConfigEntity config) {
        try {
            AudioContext audioContext = new AudioContext();

            audioContext.setType(ModelTypeEnum.gpt_sovits.getName());
            audioContext.setModelId(config.getModelId());

            audioContext.setGptSovitsConfig(config);

            audioContext.setRefAudioId(config.getMoodAudioId());
            audioContext.setText(config.getText());

            ResponseEntity<byte[]> audioResponse = audioCreator.createAudio(audioContext);

            HttpHeaders headers = audioResponse.getHeaders();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE))
                    .header("x-text-data", headers.getFirst("x-text-data"))
                    .body(audioResponse.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("msg", Base64.encode(e.getMessage().getBytes(StandardCharsets.UTF_8)))
                    .body(null);
        }
    }

    @PostMapping("createConfig")
    public Result<Object> createConfig(@RequestParam("configName") String configName,
                                       @RequestParam("temperature") Float temperature,
                                       @RequestParam("topP") Float topP,
                                       @RequestParam("topK") Integer topK,

                                       @RequestParam("repetitionPenalty") Float repetitionPenalty,
                                       @RequestParam("batchSize") Integer batchSize,
                                       @RequestParam("parallelInfer") Boolean parallelInfer,
                                       @RequestParam("splitBucket") Boolean splitBucket,
                                       @RequestParam("seed") Integer seed,
                                       @RequestParam("textSplitMethod") String textSplitMethod,
                                       @RequestParam("fragmentInterval") Float fragmentInterval,
                                       @RequestParam("speedFactor") Float speedFactor,

                                       @RequestParam("modelId") String modelId,
                                       @RequestParam("moodAudioId") String moodAudioId,

                                       @RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "saveAudio", required = false) Boolean saveAudio,
                                       @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        List<GptSovitsConfigEntity> configEntities = bGptSovitsConfigService.getByConfigName(configName);
        if (!CollectionUtils.isEmpty(configEntities)) {
            throw new BizException("配置名称[" + configName + "]已存在");
        }

        GptSovitsConfigEntity configEntity = new GptSovitsConfigEntity();
        configEntity.setConfigId(IdUtils.uuid());
        configEntity.setConfigName(configName);
        configEntity.setTemperature(temperature);
        configEntity.setTopP(topP);
        configEntity.setTopK(topK);

        configEntity.setRepetitionPenalty(repetitionPenalty);
        configEntity.setBatchSize(batchSize);
        configEntity.setParallelInfer(parallelInfer);
        configEntity.setSplitBucket(splitBucket);
        configEntity.setSeed(seed);
        configEntity.setTextSplitMethod(textSplitMethod);
        configEntity.setFragmentInterval(fragmentInterval);
        configEntity.setSpeedFactor(speedFactor);

        configEntity.setModelId(modelId);
        configEntity.setMoodAudioId(moodAudioId);

        bGptSovitsConfigService.createConfig(configEntity);

        if (Objects.equals(saveAudio, Boolean.TRUE) && file != null && StringUtils.isNotBlank(text)) {
            String fileName = FileUtils.fileNameFormat(text);
            Path voiceAudioDir = pathConfig.buildModelPath("ref-audio", ModelTypeEnum.gpt_sovits.getName(), configName, "默认", fileName + ".wav");
            if (Files.notExists(voiceAudioDir.getParent())) {
                Files.createDirectories(voiceAudioDir.getParent());
            }
            Files.write(voiceAudioDir, file.getBytes());
        }
        return Result.success();
    }
}

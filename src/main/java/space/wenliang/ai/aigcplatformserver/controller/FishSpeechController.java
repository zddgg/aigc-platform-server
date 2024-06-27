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
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechModelEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.business.BFishSpeechConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BFishSpeechModelService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("fishSpeech")
public class FishSpeechController {

    private final PathConfig pathConfig;
    private final AudioCreator audioCreator;
    private final BFishSpeechModelService bFishSpeechModelService;
    private final BFishSpeechConfigService bFishSpeechConfigService;

    public FishSpeechController(PathConfig pathConfig,
                                AudioCreator audioCreator,
                                BFishSpeechModelService bFishSpeechModelService,
                                BFishSpeechConfigService bFishSpeechConfigService) {
        this.pathConfig = pathConfig;
        this.audioCreator = audioCreator;
        this.bFishSpeechModelService = bFishSpeechModelService;
        this.bFishSpeechConfigService = bFishSpeechConfigService;
    }

    @PostMapping("modelList")
    public Result<Object> modelList() {
        List<FishSpeechModelEntity> models = bFishSpeechModelService.list();
        return Result.success(models);
    }

    @PostMapping("refreshCache")
    public Result<Object> refreshCache() {
        bFishSpeechModelService.refreshCache();
        return Result.success();
    }

    @PostMapping("configs")
    public Result<Object> configs() {
        return Result.success(bFishSpeechConfigService.configs());
    }

    @PostMapping("deleteConfig")
    public Result<Object> deleteConfig(@RequestBody FishSpeechConfigEntity config) {
        bFishSpeechConfigService.deleteConfig(config);
        return Result.success();
    }

    @PostMapping("playAudio")
    public ResponseEntity<byte[]> playAudio(@RequestBody FishSpeechConfigEntity config) {
        try {
            AudioContext audioContext = new AudioContext();

            audioContext.setType(ModelTypeEnum.fish_speech.getName());
            audioContext.setModelId(config.getModelId());

            audioContext.setFishSpeechConfig(config);

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
    public Result<Object> createConfig(@RequestParam(value = "id", required = false) Integer id,
                                       @RequestParam("configName") String configName,

                                       @RequestParam("temperature") Float temperature,
                                       @RequestParam("topP") Float topP,
                                       @RequestParam("repetitionPenalty") Float repetitionPenalty,

                                       @RequestParam("modelId") String modelId,
                                       @RequestParam("moodAudioId") String moodAudioId,

                                       @RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "saveAudio", required = false) Boolean saveAudio,
                                       @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        FishSpeechConfigEntity configEntity = new FishSpeechConfigEntity();

        if (Objects.isNull(id)) {
            List<FishSpeechConfigEntity> configEntities = bFishSpeechConfigService.getByConfigName(configName);
            if (!CollectionUtils.isEmpty(configEntities)) {
                throw new BizException("配置名称[" + configName + "]已存在");
            }
            configEntity.setConfigId(IdUtils.uuid());
        }

        configEntity.setId(id);
        configEntity.setConfigName(configName);

        configEntity.setTemperature(temperature);
        configEntity.setTopP(topP);
        configEntity.setRepetitionPenalty(repetitionPenalty);

        configEntity.setModelId(modelId);
        configEntity.setMoodAudioId(moodAudioId);

        bFishSpeechConfigService.createOrUpdate(configEntity);

        if (Objects.equals(saveAudio, Boolean.TRUE) && file != null && StringUtils.isNotBlank(text)) {
            String fileName = FileUtils.fileNameFormat(text);
            Path voiceAudioDir = pathConfig.buildModelPath(
                    "ref-audio", ModelTypeEnum.fish_speech.getName(), configName, "默认", fileName + ".wav");
            if (Files.notExists(voiceAudioDir.getParent())) {
                Files.createDirectories(voiceAudioDir.getParent());
            }
            Files.write(voiceAudioDir, file.getBytes());
        }
        return Result.success();
    }
}

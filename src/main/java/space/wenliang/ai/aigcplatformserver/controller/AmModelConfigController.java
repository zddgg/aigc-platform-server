package space.wenliang.ai.aigcplatformserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.bean.LangDict;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.AmModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.AmModelFileEntity;
import space.wenliang.ai.aigcplatformserver.entity.AmPromptAudioEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.AmModelConfigService;
import space.wenliang.ai.aigcplatformserver.service.AmModelFileService;
import space.wenliang.ai.aigcplatformserver.service.AmPromptAudioService;
import space.wenliang.ai.aigcplatformserver.service.cache.DictService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static space.wenliang.ai.aigcplatformserver.common.CommonConstants.prompt_audio;

@Slf4j
@RestController
@RequestMapping("amModelConfig")
@RequiredArgsConstructor
public class AmModelConfigController {

    private final EnvConfig envConfig;
    private final AudioCreator audioCreator;
    private final AmModelFileService amModelFileService;
    private final AmModelConfigService amModelConfigService;
    private final AmPromptAudioService amPromptAudioService;
    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final DictService dictService;

    @PostMapping("getByModelType")
    public Result<Object> getByModelType(@SingleValueParam("modelType") String modelType,
                                         @SingleValueParam("showMode") Integer showMode) {
        List<AmModelConfigEntity> modelConfigEntities = amModelConfigService.getByModelType(modelType, showMode);
        return Result.success(modelConfigEntities);
    }

    @PostMapping("createConfig")
    public Result<Object> createConfig(@RequestParam(value = "id", required = false) Integer id,

                                       @RequestParam("amType") String amType,
                                       @RequestParam(value = "mfId", required = false) String mfId,
                                       @RequestParam(value = "paId", required = false) String paId,

                                       @RequestParam("mcName") String mcName,
                                       @RequestParam("mcParamsJson") String mcParamsJson,

                                       @RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "saveAudio", required = false) Boolean saveAudio,
                                       @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        AmModelConfigEntity configEntity = new AmModelConfigEntity();
        if (Objects.isNull(id)) {
            List<AmModelConfigEntity> configEntities = amModelConfigService.list(
                    new LambdaQueryWrapper<AmModelConfigEntity>()
                            .eq(AmModelConfigEntity::getAmType, amType)
                            .eq(AmModelConfigEntity::getMcName, mcName));
            if (!CollectionUtils.isEmpty(configEntities)) {
                throw new BizException("配置名称[" + mcName + "]已存在");
            }
            configEntity.setMcId(IdUtils.uuid());
        } else {
            configEntity.setId(id);
        }

        configEntity.setMcName(mcName);
        configEntity.setMcParamsJson(mcParamsJson);
        configEntity.setAmType(amType);
        configEntity.setMfId(mfId);
        configEntity.setPaId(paId);
        configEntity.setText(text);

        boolean saveAudioValue = Objects.equals(saveAudio, Boolean.TRUE) && file != null && StringUtils.isNotBlank(text);

        configEntity.setSaveAudio(saveAudioValue);

        amModelConfigService.saveOrUpdate(configEntity);

        if (saveAudioValue) {
            String fileName = FileUtils.fileNameFormat(text);
            Path voiceAudioDir = envConfig.buildModelPath(prompt_audio, amType, mcName, "默认", fileName + ".wav");
            if (Files.notExists(voiceAudioDir.getParent())) {
                Files.createDirectories(voiceAudioDir.getParent());
            }
            Files.write(voiceAudioDir, file.getBytes());
        }
        return Result.success();
    }

    @PostMapping("updateConfig")
    public Result<Object> updateConfig(@RequestBody AmModelConfigEntity modelConfig) {
        amModelConfigService.updateConfig(modelConfig);
        return Result.success();
    }

    @PostMapping("deleteConfig")
    public Result<Object> deleteConfig(@RequestBody AmModelConfigEntity modelConfig) throws IOException {
        amModelConfigService.removeById(modelConfig);
        Path path = envConfig.buildModelPath(
                prompt_audio,
                modelConfig.getAmType(),
                FileUtils.fileNameFormat(modelConfig.getMcName()));
        FileUtils.deleteDirectoryAll(path);
        return Result.success();
    }

    @PostMapping("createAudio")
    public ResponseEntity<byte[]> createAudio(@SingleValueParam("amType") String amType,
                                              @SingleValueParam("mfId") String mfId,
                                              @SingleValueParam("paId") String paId,
                                              @SingleValueParam("mcParamsJson") String mcParamsJson,
                                              @SingleValueParam("text") String text) {
        try {
            AmModelFileEntity modelFile = null;
            if (StringUtils.isNotBlank(mfId)) {
                modelFile = amModelFileService.getOne(new LambdaQueryWrapper<AmModelFileEntity>()
                        .eq(AmModelFileEntity::getMfId, mfId));
            }

            AmPromptAudioEntity promptAudio = null;
            if (StringUtils.isNotBlank(paId)) {
                promptAudio = amPromptAudioService.getOne(new LambdaQueryWrapper<AmPromptAudioEntity>()
                        .eq(AmPromptAudioEntity::getPaId, paId));
            }

            AudioContext audioContext = new AudioContext();

            audioContext.setText(text);
            audioContext.setTextLang("zh");

            audioContext.setAmType(amType);

            audioContext.setPromptAudio(promptAudio);
            audioContext.setModelFile(modelFile);
            audioContext.setAmMcParamsJson(mcParamsJson);

            ResponseEntity<byte[]> audioResponse = audioCreator.createAudio(audioContext);

            HttpHeaders headers = audioResponse.getHeaders();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE))
                    .header("x-text-data", headers.getFirst("x-text-data"))
                    .body(audioResponse.getBody());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            globalWebSocketHandler.sendErrorMessage("音频生成异常", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("playOrCreateAudio")
    public ResponseEntity<byte[]> playOrCreateAudio(@SingleValueParam("mcId") String mcId) {
        try {
            AmModelConfigEntity modelConfig = amModelConfigService.getByMcId(mcId);

            String text;
            String textLang;

            if (StringUtils.equals(modelConfig.getAmType(), ModelTypeEnum.edge_tts.getName())) {
                textLang = modelConfig.getMcName().substring(0, modelConfig.getMcName().indexOf("-"));
            } else {
                textLang = "zh";
            }

            LangDict langDict = dictService.getLangDict(textLang);
            if (Objects.isNull(langDict) || StringUtils.isBlank(langDict.getText())) {
                throw new BizException("没有找到[" + textLang + "]对应的语言或示例文本");
            }

            if (StringUtils.isBlank(modelConfig.getText())) {
                text = langDict.getText();
            } else {
                text = modelConfig.getText();
            }

            if (StringUtils.isNotBlank(text)) {
                Path path = envConfig.buildModelPath(
                        prompt_audio,
                        modelConfig.getAmType(),
                        FileUtils.fileNameFormat(modelConfig.getMcName()),
                        "默认",
                        FileUtils.fileNameFormat(text) + ".wav"
                );

                if (Files.exists(path)) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                            .body(Files.readAllBytes(path));
                }
            }

            AmModelFileEntity modelFile = null;
            if (StringUtils.isNotBlank(modelConfig.getMfId())) {
                modelFile = amModelFileService.getOne(new LambdaQueryWrapper<AmModelFileEntity>()
                        .eq(AmModelFileEntity::getMfId, modelConfig.getMfId()));
            }

            AmPromptAudioEntity promptAudio = null;
            if (StringUtils.isNotBlank(modelConfig.getPaId())) {
                promptAudio = amPromptAudioService.getOne(new LambdaQueryWrapper<AmPromptAudioEntity>()
                        .eq(AmPromptAudioEntity::getPaId, modelConfig.getPaId()));
            }

            AudioContext audioContext = new AudioContext();
            audioContext.setText(text);
            audioContext.setTextLang(textLang);

            audioContext.setAmType(modelConfig.getAmType());

            audioContext.setPromptAudio(promptAudio);
            audioContext.setModelFile(modelFile);
            audioContext.setAmMcParamsJson(modelConfig.getMcParamsJson());

            ResponseEntity<byte[]> audioResponse = audioCreator.createAudio(audioContext);

            if (Objects.nonNull(audioResponse.getBody())) {
                Path path = envConfig.buildModelPath(
                        prompt_audio,
                        modelConfig.getAmType(),
                        FileUtils.fileNameFormat(modelConfig.getMcName()),
                        "默认",
                        FileUtils.fileNameFormat(text) + ".wav"
                );
                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, audioResponse.getBody());
            }

            HttpHeaders headers = audioResponse.getHeaders();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE))
                    .header("x-text-data", headers.getFirst("x-text-data"))
                    .body(audioResponse.getBody());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            globalWebSocketHandler.sendErrorMessage("音频生成异常", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("syncEdgeTtsConfig")
    public Result<Object> syncEdgeTtsConfig() throws IOException {
        amModelConfigService.syncEdgeTtsConfig();
        return Result.success();
    }

    @PostMapping("updateEdgeTtsShowFlag")
    public Result<Object> updateEdgeTtsShowFlag(@RequestBody Map<String, Boolean> data) {
        amModelConfigService.updateEdgeTtsShowFlag(data);
        return Result.success();
    }
}

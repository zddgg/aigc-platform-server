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
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.business.BChatTtsService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("chatTts")
public class ChatTtsController {

    private final PathConfig pathConfig;
    private final AudioCreator audioCreator;
    private final BChatTtsService bChatTtsService;

    public ChatTtsController(PathConfig pathConfig, AudioCreator audioCreator, BChatTtsService bChatTtsService) {
        this.pathConfig = pathConfig;
        this.audioCreator = audioCreator;
        this.bChatTtsService = bChatTtsService;
    }

    @PostMapping("configs")
    public Result<Object> configs() {
        return Result.success(bChatTtsService.configs());
    }

    @PostMapping("deleteConfig")
    public Result<Object> deleteConfig(@RequestBody ChatTtsConfigEntity config) {
        bChatTtsService.deleteConfig(config);
        return Result.success();
    }

    @PostMapping("createConfig")
    public Result<Object> createConfig(@RequestParam(value = "id", required = false) Integer id,
                                       @RequestParam("configName") String configName,
                                       @RequestParam("temperature") Float temperature,
                                       @RequestParam("topP") Float topP,
                                       @RequestParam("topK") Integer topK,
                                       @RequestParam("audioSeedInput") Integer audioSeedInput,
                                       @RequestParam("textSeedInput") Integer textSeedInput,
                                       @RequestParam("refineTextFlag") Boolean refineTextFlag,
                                       @RequestParam("refineTextParams") String refineTextParams,

                                       @RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "saveAudio", required = false) Boolean saveAudio,
                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam(value = "outputText", required = false) String outputText) throws Exception {

        ChatTtsConfigEntity chatTtsConfig = new ChatTtsConfigEntity();

        if (Objects.isNull(id)) {
            List<ChatTtsConfigEntity> chatTtsConfigs = bChatTtsService.getByConfigName(configName);
            if (!CollectionUtils.isEmpty(chatTtsConfigs)) {
                throw new BizException("配置名称[" + configName + "]已存在");
            }
            chatTtsConfig.setConfigId(IdUtils.uuid());
        }

        chatTtsConfig.setId(id);
        chatTtsConfig.setConfigName(configName);

        chatTtsConfig.setTemperature(temperature);
        chatTtsConfig.setTopP(topP);
        chatTtsConfig.setTopK(topK);
        chatTtsConfig.setAudioSeedInput(audioSeedInput);
        chatTtsConfig.setTextSeedInput(textSeedInput);
        chatTtsConfig.setRefineTextFlag(refineTextFlag);
        chatTtsConfig.setRefineTextParams(refineTextParams);

        chatTtsConfig.setText(text);
        chatTtsConfig.setOutputText(outputText);

        bChatTtsService.createOrUpdate(chatTtsConfig);

        if (Objects.equals(saveAudio, Boolean.TRUE) && file != null && StringUtils.isNotBlank(outputText)) {
            String fileName = FileUtils.fileNameFormat(outputText);
            Path voiceAudioDir = pathConfig.buildModelPath("ref-audio", "chat-tts", configName, "默认", fileName + ".wav");
            if (Files.notExists(voiceAudioDir.getParent())) {
                Files.createDirectories(voiceAudioDir.getParent());
            }
            Files.write(voiceAudioDir, file.getBytes());
        }
        return Result.success();
    }

    @PostMapping("playAudio")
    public ResponseEntity<byte[]> playAudio(@RequestBody ChatTtsConfigEntity config) {
        try {
            AudioContext audioContext = new AudioContext();

            audioContext.setType(ModelTypeEnum.chat_tts.getName());

            audioContext.setChatTtsConfig(config);

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
}

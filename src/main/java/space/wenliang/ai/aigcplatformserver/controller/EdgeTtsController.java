package space.wenliang.ai.aigcplatformserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;
import space.wenliang.ai.aigcplatformserver.service.business.BEdgeTtsService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.util.List;

@RestController
@RequestMapping("edgeTts")
public class EdgeTtsController {

    private static final Logger log = LoggerFactory.getLogger(EdgeTtsController.class);
    private final AudioCreator audioCreator;
    private final BEdgeTtsService bEdgeTtsService;
    private final GlobalWebSocketHandler globalWebSocketHandler;

    public EdgeTtsController(AudioCreator audioCreator,
                             BEdgeTtsService bEdgeTtsService,
                             GlobalWebSocketHandler globalWebSocketHandler) {
        this.audioCreator = audioCreator;
        this.bEdgeTtsService = bEdgeTtsService;
        this.globalWebSocketHandler = globalWebSocketHandler;
    }

    @PostMapping("configs")
    public Result<List<EdgeTtsConfigEntity>> configs() {
        List<EdgeTtsConfigEntity> configs = bEdgeTtsService.configs();
        return Result.success(configs);
    }

    @PostMapping("settings")
    public Result<List<EdgeTtsSettingEntity>> settings() {
        List<EdgeTtsSettingEntity> settings = bEdgeTtsService.settings();
        return Result.success(settings);
    }

    @PostMapping("updateSetting")
    public Result<Object> updateSetting(@RequestBody EdgeTtsSettingEntity edgeTtsSettingEntity) {
        bEdgeTtsService.updateSetting(edgeTtsSettingEntity);
        return Result.success();
    }

    @PostMapping("syncConfigs")
    public Result<Object> syncConfigs() {
        bEdgeTtsService.syncConfigs();
        return Result.success();
    }

    @PostMapping("playOrCreateAudio")
    public Result<Object> playOrCreateAudio(@SingleValueParam("voice") String voice) {
        String audioUrl = bEdgeTtsService.playOrCreateAudio(voice);
        return Result.success(audioUrl);
    }

    @PostMapping("playAudio")
    public ResponseEntity<byte[]> playAudio(@RequestBody EdgeTtsConfigEntity config) {
        try {
            AudioContext audioContext = new AudioContext();

            audioContext.setType(ModelTypeEnum.edge_tts.getName());

            audioContext.setEdgeTtsConfig(config);

            audioContext.setText(config.getText());

            ResponseEntity<byte[]> audioResponse = audioCreator.createAudio(audioContext);
            HttpHeaders headers = audioResponse.getHeaders();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE))
                    .body(audioResponse.getBody());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            globalWebSocketHandler.sendErrorMessage(e.getMessage());
            return ResponseEntity.ok().body(null);
        }
    }
}

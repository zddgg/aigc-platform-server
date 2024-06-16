package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;
import space.wenliang.ai.aigcplatformserver.service.business.BEdgeTtsService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.util.List;

@RestController
@RequestMapping("edgeTts")
public class EdgeTtsController {

    private final BEdgeTtsService bEdgeTtsService;

    public EdgeTtsController(BEdgeTtsService bEdgeTtsService) {
        this.bEdgeTtsService = bEdgeTtsService;
    }

    @PostMapping("configs")
    public Result<List<EdgeTtsConfigEntity>> configs() {
        return Result.success(bEdgeTtsService.configs());
    }

    @PostMapping("settings")
    public Result<List<EdgeTtsSettingEntity>> settings() {
        return Result.success(bEdgeTtsService.settings());
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
}

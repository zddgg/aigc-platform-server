package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.AudioServerConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.business.BAudioServerConfigService;

import java.util.List;

@RestController
@RequestMapping("audioServerConfig")
public class AudioServerConfigController {

    private final BAudioServerConfigService bAudioServerConfigService;

    public AudioServerConfigController(BAudioServerConfigService bAudioServerConfigService) {
        this.bAudioServerConfigService = bAudioServerConfigService;
    }

    @PostMapping("list")
    public Result<Object> list() {
        List<AudioServerConfigEntity> list = bAudioServerConfigService.list();
        return Result.success(list);
    }

    @PostMapping("updateConfig")
    public Result<Object> updateConfig(@RequestBody AudioServerConfigEntity config) {
        bAudioServerConfigService.updateConfig(config);
        return Result.success();
    }
}

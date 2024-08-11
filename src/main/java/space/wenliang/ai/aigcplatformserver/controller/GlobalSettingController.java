package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.GlobalSetting;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;

@RestController
@RequestMapping("globalSetting")
@RequiredArgsConstructor
public class GlobalSettingController {

    private final GlobalSettingService globalSettingService;

    @PostMapping("getGlobalSetting")
    public Result<Object> getGlobalSetting() {
        return Result.success(globalSettingService.getGlobalSetting());
    }

    @PostMapping("updateGlobalSetting")
    public Result<Object> updateGlobalSetting(@RequestBody GlobalSetting globalSetting) {
        globalSettingService.updateGlobalSetting(globalSetting);
        return Result.success();
    }
}

package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.AppInfoData;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;

@RestController
@RequestMapping("app")
@RequiredArgsConstructor
public class AppController {

    private final EnvConfig envConfig;

    @PostMapping("appInfo")
    public Result<Object> getAppInfoData() {
        AppInfoData appInfoData = AppInfoData.builder()
                .currentVersion(envConfig.getApplicationVersion())
                .build();
        return Result.success(appInfoData);
    }
}

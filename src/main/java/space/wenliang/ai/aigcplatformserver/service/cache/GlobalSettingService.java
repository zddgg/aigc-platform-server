package space.wenliang.ai.aigcplatformserver.service.cache;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GlobalSetting;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.hooks.ShutdownHook;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalSettingService implements StartHook.StartHookListener, ShutdownHook.ShutdownHookListener {

    private static GlobalSetting globalSetting = new GlobalSetting();

    private final EnvConfig envConfig;

    @Override
    public void startHook() throws Exception {
        Path path = envConfig.buildConfigPath("global-setting.json");
        if (Files.exists(path)) {
            try {
                globalSetting = FileUtils.getObjectFromFile(path, GlobalSetting.class);
            } catch (Exception e) {
                log.error("读取[global-setting.json]失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void shutdownHook() throws Exception {
        Path path = envConfig.buildConfigPath("global-setting.json");
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, JSON.toJSONBytes(globalSetting));
    }

    public GlobalSetting getGlobalSetting() {
        return globalSetting;
    }

    public void updateGlobalSetting(GlobalSetting globalSetting) {
        GlobalSettingService.globalSetting = globalSetting;
    }
}

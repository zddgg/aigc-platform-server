package space.wenliang.ai.aigcplatformserver.hooks.start;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;
import space.wenliang.ai.aigcplatformserver.service.AmModelFileService;
import space.wenliang.ai.aigcplatformserver.service.AmPromptAudioService;

@Component
@RequiredArgsConstructor
public class CacheLoad implements StartHook.StartHookListener {

    private final AmModelFileService amModelFileService;
    private final AmPromptAudioService amPromptAudioService;

    @Override
    public void startHook() {
        amModelFileService.refreshCache(ModelTypeEnum.gpt_sovits.getName());
        amPromptAudioService.refreshCache();
    }
}

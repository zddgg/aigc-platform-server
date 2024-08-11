package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudio;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudioSort;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.AmPromptAudioService;

import java.util.List;

@RestController
@RequestMapping("amPromptAudio")
@RequiredArgsConstructor
public class AmPromptAudioController {

    private final AmPromptAudioService amPromptAudioService;

    @PostMapping("refreshCache")
    public Result<Object> refreshCache() {
        amPromptAudioService.refreshCache();
        return Result.success();
    }

    @PostMapping("queryPromptAudios")
    public Result<Object> queryPromptAudios() {
        List<PromptAudio> promptAudios = amPromptAudioService.promptAudios();
        return Result.success(promptAudios);
    }

    @PostMapping("updatePromptAudio")
    public Result<Object> updatePromptAudio(@RequestBody PromptAudio promptAudio) {
        amPromptAudioService.updatePromptAudio(promptAudio);
        return Result.success();
    }

    @PostMapping("queryPromptAudioSorts")
    public Result<Object> queryPromptAudioSorts() {
        List<PromptAudioSort> refAudioSorts = amPromptAudioService.queryPromptAudioSorts();
        return Result.success(refAudioSorts);
    }

    @PostMapping("updatePromptAudioSorts")
    public Result<Object> updatePromptAudioSorts(@RequestBody List<PromptAudioSort> promptAudioSorts) {
        amPromptAudioService.updatePromptAudioSorts(promptAudioSorts);
        return Result.success();
    }
}

package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.RefAudio;
import space.wenliang.ai.aigcplatformserver.bean.RefAudioSort;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.business.BRefAudioService;

import java.util.List;

@RestController
@RequestMapping("refAudio")
public class RefAudioController {

    private final BRefAudioService bRefAudioService;

    public RefAudioController(BRefAudioService bRefAudioService) {
        this.bRefAudioService = bRefAudioService;
    }

    @PostMapping("refAudioList")
    public Result<Object> refAudioList() {
        return Result.success(bRefAudioService.refAudioList());
    }

    @PostMapping("updateRefAudio")
    public Result<Object> updateRefAudio(@RequestBody RefAudio refAudio) {
        bRefAudioService.updateRefAudio(refAudio);
        return Result.success();
    }

    @PostMapping("refreshCache")
    public Result<Object> refreshCache() {
        bRefAudioService.refreshCache();
        return Result.success();
    }

    @PostMapping("queryGroupSorts")
    public Result<Object> queryGroupSorts() {
        List<RefAudioSort> refAudioSorts = bRefAudioService.queryGroupSorts();
        return Result.success(refAudioSorts);
    }

    @PostMapping("updateRefAudioSorts")
    public Result<Object> updateRefAudioSorts(@RequestBody List<RefAudioSort> refAudioSorts) {
        bRefAudioService.updateRefAudioSorts(refAudioSorts);
        return Result.success();
    }
}

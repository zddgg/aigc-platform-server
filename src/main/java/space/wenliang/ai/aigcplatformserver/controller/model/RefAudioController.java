package space.wenliang.ai.aigcplatformserver.controller.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.RefAudio;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;
import space.wenliang.ai.aigcplatformserver.service.ModelService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("model/refAudio")
public class RefAudioController {

    private final ModelService modelService;
    private final ConfigService configService;

    public RefAudioController(ModelService modelService, ConfigService configService) {
        this.modelService = modelService;
        this.configService = configService;
    }

    @PostMapping("queryRefAudios")
    public Result<Object> queryRefAudios() throws IOException {
        List<RefAudio> refAudios = modelService.getAudios();
        return Result.success(refAudios);
    }

    @PostMapping("updateRefAudio")
    public Result<Object> updateRefAudio(@RequestBody RefAudio refAudio) throws Exception {

        List<RefAudio> refAudioConfigs = configService.getRefAudioConfig();

        for (RefAudio refAudioConfig : refAudioConfigs) {
            if (StringUtils.equals(refAudio.getGroup() + "-" + refAudio.getName(),
                    refAudioConfig.getGroup() + "-" + refAudioConfig.getName())) {
                BeanUtils.copyProperties(refAudio, refAudioConfig);
            }
        }

        configService.saveRefAudioConfig(refAudioConfigs);
        return Result.success();
    }
}

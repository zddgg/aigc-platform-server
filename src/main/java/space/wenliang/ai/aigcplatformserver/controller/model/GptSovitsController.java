package space.wenliang.ai.aigcplatformserver.controller.model;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.GsvModel;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.ModelService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("model/gpt-sovits")
public class GptSovitsController {

    private final ModelService modelService;

    public GptSovitsController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("queryModels")
    public Result<Object> queryModels() throws IOException {
        List<GsvModel> models = modelService.getModels("gpt-sovits");
        return Result.success(models);
    }
}

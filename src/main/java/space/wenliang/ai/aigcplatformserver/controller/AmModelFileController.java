package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.AmModelFileEntity;
import space.wenliang.ai.aigcplatformserver.service.AmModelFileService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.util.List;

@RestController
@RequestMapping("amModelFile")
@RequiredArgsConstructor
public class AmModelFileController {

    private final AmModelFileService amModelFileService;

    @PostMapping("refreshCache")
    public Result<Object> refreshCache(@SingleValueParam("modelType") String modelType) {
        amModelFileService.refreshCache(modelType);
        return Result.success();
    }

    @PostMapping("getByModelType")
    public Result<Object> getByModelType(@SingleValueParam("modelType") String modelType) {
        List<AmModelFileEntity> modelFiles = amModelFileService.getByModelType(modelType);
        return Result.success(modelFiles);
    }
}

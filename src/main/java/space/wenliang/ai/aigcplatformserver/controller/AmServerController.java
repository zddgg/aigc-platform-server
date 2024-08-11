package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.AmServerEntity;
import space.wenliang.ai.aigcplatformserver.service.AmServerService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("amServer")
@RequiredArgsConstructor
public class AmServerController {

    private final AmServerService amServerService;

    @PostMapping("list")
    public Result<Object> list() {
        List<AmServerEntity> list = amServerService.list();
        return Result.success(list);
    }

    @PostMapping("updateConfig")
    public Result<Object> updateConfig(@RequestBody AmServerEntity config) {
        amServerService.updateById(config);
        return Result.success();
    }
}

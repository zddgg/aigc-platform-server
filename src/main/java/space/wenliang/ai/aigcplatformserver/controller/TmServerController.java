package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.TmServerEntity;
import space.wenliang.ai.aigcplatformserver.service.TmServerService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("tmServer")
@RequiredArgsConstructor
public class TmServerController {

    private final EnvConfig envConfig;
    private final TmServerService tmServerService;

    @PostMapping("list")
    public Result<Object> list() {
        return Result.success(tmServerService.list());
    }

    @PostMapping("templateList")
    public Result<Object> templateList() {
        List<Object> tmServerEntities = new ArrayList<>();

        Path path = envConfig.buildConfigPath("text-model-server-template.json");

        if (Files.exists(path)) {
            try {
                tmServerEntities = FileUtils.getListFromFile(path, Object.class);
            } catch (Exception e) {
                log.error("读取文本大模型服务模板失败", e);
                throw new RuntimeException(e);
            }
        }
        return Result.success(tmServerEntities);
    }

    @PostMapping("updateModelChatConfig")
    public Result<Object> updateModelChatConfig(@RequestBody TmServerEntity tmServerEntity) {
        tmServerService.saveOrUpdate(tmServerEntity);

        return Result.success();
    }

    @PostMapping("deleteModelChatConfig")
    public Result<Object> deleteModelChatConfig(@RequestBody TmServerEntity tmServerEntity) {
        tmServerService.removeById(tmServerEntity);

        return Result.success();
    }

    @PostMapping("activeModelChatConfig")
    public Result<Object> activeModelChatConfig(@RequestBody TmServerEntity tmServerEntity) {
        List<TmServerEntity> tmServerEntities = tmServerService.list();

        for (TmServerEntity serverEntity : tmServerEntities) {
            serverEntity.setActive(Objects.equals(tmServerEntity.getId(), serverEntity.getId()));
        }

        tmServerService.updateBatchById(tmServerEntities);

        return Result.success();
    }
}

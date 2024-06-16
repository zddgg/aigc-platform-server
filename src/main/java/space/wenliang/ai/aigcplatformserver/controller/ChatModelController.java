package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.business.BChatModelConfigService;

@RestController
@RequestMapping("chat")
public class ChatModelController {

    private final BChatModelConfigService bChatModelConfigService;

    public ChatModelController(BChatModelConfigService bChatModelConfigService) {
        this.bChatModelConfigService = bChatModelConfigService;
    }

    @PostMapping("list")
    public Result<Object> list() {
        return Result.success(bChatModelConfigService.list());
    }

    @PostMapping("templateList")
    public Result<Object> templateList() {
        return Result.success(bChatModelConfigService.templateList());
    }

    @PostMapping("updateModelChatConfig")
    public Result<Object> updateModelChatConfig(@RequestBody ChatModelConfigEntity chatModelConfigEntity) {
        bChatModelConfigService.updateModelChatConfig(chatModelConfigEntity);
        return Result.success();
    }

    @PostMapping("deleteModelChatConfig")
    public Result<Object> deleteModelChatConfig(@RequestBody ChatModelConfigEntity chatModelConfigEntity) {
        bChatModelConfigService.deleteModelChatConfig(chatModelConfigEntity);

        return Result.success();
    }

    @PostMapping("activeModelChatConfig")
    public Result<Object> activeModelChatConfig(@RequestBody ChatModelConfigEntity chatModelConfigEntity) {
        bChatModelConfigService.activeModelChatConfig(chatModelConfigEntity);

        return Result.success();
    }
}

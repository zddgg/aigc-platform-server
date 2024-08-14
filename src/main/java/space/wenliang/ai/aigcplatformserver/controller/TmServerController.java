package space.wenliang.ai.aigcplatformserver.controller;

import com.alibaba.fastjson2.JSON;
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

    public static String textModelServerTemplateStr = """
            [
              {
                "templateName": "OpenAI",
                "interfaceType": "OpenAi",
                "host": "https://api.openai.com",
                "path": "/v1/chat/completions",
                "model": "gpt-3.5-turbo",
                "temperature": 0.3,
                "maxTokens": 4096
              },
              {
                "templateName": "Kimi",
                "interfaceType": "OpenAi",
                "host": "https://api.moonshot.cn",
                "path": "/v1/chat/completions",
                "model": "moonshot-v1-32k",
                "temperature": 0.3,
                "maxTokens": 0
              },
              {
                "templateName": "DeepSeek",
                "interfaceType": "OpenAi",
                "host": "https://api.deepseek.com",
                "path": "/v1/chat/completions",
                "model": "deepseek-chat",
                "temperature": 0.3,
                "maxTokens": 4096
              },
              {
                "templateName": "Ollama",
                "interfaceType": "OpenAi",
                "host": "http://127.0.0.1:11434",
                "path": "/v1/chat/completions",
                "model": "qwen2:7b",
                "temperature": 0.3,
                "maxTokens": 4096
              },
              {
                "templateName": "Glm",
                "interfaceType": "OpenAi",
                "host": "https://open.bigmodel.cn",
                "path": "/api/paas/v4/chat/completions",
                "model": "glm-4",
                "temperature": 0.3,
                "maxTokens": 4096
              },
              {
                "templateName": "Spark",
                "interfaceType": "Spark",
                "host": "https://spark-api.xf-yun.com",
                "path": "/v3.5/chat",
                "model": "generalv3.5",
                "temperature": 0.3,
                "maxTokens": 4096
              },
              {
                "templateName": "Qwen",
                "interfaceType": "Qwen",
                "host": "https://dashscope.aliyuncs.com",
                "path": "/api/v1/services/aigc/text-generation/generation",
                "model": "qwen-turbo",
                "temperature": 0.3,
                "maxTokens": 2000
              }
            ]
            """;
    private final EnvConfig envConfig;
    private final TmServerService tmServerService;

    @PostMapping("list")
    public Result<Object> list() {
        return Result.success(tmServerService.list());
    }

    @PostMapping("templateList")
    public Result<Object> templateList() {
        List<Object> tmServerEntities;

        Path path = envConfig.buildConfigPath("text-model-server-template.json");

        if (Files.exists(path)) {
            try {
                tmServerEntities = FileUtils.getListFromFile(path, Object.class);
            } catch (Exception e) {
                log.error("读取文本大模型服务模板失败", e);
                throw new RuntimeException(e);
            }
        } else {
            tmServerEntities = JSON.parseArray(textModelServerTemplateStr);
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

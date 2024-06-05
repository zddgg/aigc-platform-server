package space.wenliang.ai.aigcplatformserver.model.chat;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatConfig;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatModelParam;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AiService {

    private final Map<String, IAiService> aiServiceMap;
    private final ConfigService configService;

    public AiService(Map<String, IAiService> aiServiceMap, ConfigService configService) {
        this.aiServiceMap = aiServiceMap;
        this.configService = configService;
    }

    public Flux<String> stream(String systemMessage, String userMessage) {
        ChatConfig chatConfig = configService.getChatConfig();
        List<ChatModelParam> services = chatConfig.getServices();
        Optional<ChatModelParam> first = services.stream()
                .filter(chatModelParam -> Objects.equals(chatModelParam.getActive(), Boolean.TRUE)).findFirst();
        if (first.isEmpty()) {
            throw new RuntimeException("No active chat service found");
        }
        return aiServiceMap.get(first.get().getInterfaceType()).stream(first.get(), systemMessage, userMessage);
    }
}

package space.wenliang.ai.aigcplatformserver.ai.chat;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AChatModelConfigService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AiService {

    private final Map<String, IAiService> aiServiceMap;
    private final AChatModelConfigService aChatModelConfigService;

    public AiService(Map<String, IAiService> aiServiceMap,
                     AChatModelConfigService aChatModelConfigService) {
        this.aiServiceMap = aiServiceMap;
        this.aChatModelConfigService = aChatModelConfigService;
    }

    public Flux<String> stream(String systemMessage, String userMessage) {
        List<ChatModelConfigEntity> services = aChatModelConfigService.list();
        Optional<ChatModelConfigEntity> first = services.stream()
                .filter(chatModelParam -> Objects.equals(chatModelParam.getActive(), Boolean.TRUE)).findFirst();
        if (first.isEmpty()) {
            throw new RuntimeException("No active chat service found");
        }
        return aiServiceMap.get(first.get().getInterfaceType()).stream(first.get(), systemMessage, userMessage);
    }
}

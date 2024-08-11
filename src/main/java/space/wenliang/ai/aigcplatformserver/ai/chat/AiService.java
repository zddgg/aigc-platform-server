package space.wenliang.ai.aigcplatformserver.ai.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.entity.TmServerEntity;
import space.wenliang.ai.aigcplatformserver.service.TmServerService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiService {

    private final Map<String, IAiService> aiServiceMap;
    private final TmServerService aChatModelConfigService;

    public Flux<String> stream(String systemMessage, String userMessage) {
        List<TmServerEntity> services = aChatModelConfigService.list();
        Optional<TmServerEntity> first = services.stream()
                .filter(chatModelParam -> Objects.equals(chatModelParam.getActive(), Boolean.TRUE)).findFirst();
        if (first.isEmpty()) {
            throw new RuntimeException("No active chat service found");
        }
        return aiServiceMap.get(first.get().getInterfaceType()).stream(first.get(), systemMessage, userMessage);
    }
}

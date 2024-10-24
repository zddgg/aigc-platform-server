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

    public Flux<String> stream(Integer tmServerId, String systemMessage, String userMessage) {
        List<TmServerEntity> services = aChatModelConfigService.list();
        Optional<TmServerEntity> first = services.stream()
                .filter(chatModelParam -> Objects.equals(chatModelParam.getId(), tmServerId)).findFirst();
        if (first.isEmpty()) {
            return Flux.error(new RuntimeException("需要有一个文本大模型配置！"));
        }
        return aiServiceMap.get(first.get().getInterfaceType()).stream(first.get(), systemMessage, userMessage);
    }
}

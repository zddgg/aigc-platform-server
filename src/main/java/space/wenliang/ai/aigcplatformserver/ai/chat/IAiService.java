package space.wenliang.ai.aigcplatformserver.ai.chat;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

public interface IAiService {

    Flux<String> call(ChatModelConfigEntity config, String systemMessage, String userMessage);

    Flux<String> stream(ChatModelConfigEntity config, String systemMessage, String userMessage);
}

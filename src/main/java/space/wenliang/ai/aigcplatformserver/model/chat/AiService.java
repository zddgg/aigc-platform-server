package space.wenliang.ai.aigcplatformserver.model.chat;

import reactor.core.publisher.Flux;

public interface AiService {

    Flux<String> call(String systemMessage, String userMessage);

    Flux<String> stream(String systemMessage, String userMessage);
}

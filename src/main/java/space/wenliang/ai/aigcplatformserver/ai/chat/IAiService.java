package space.wenliang.ai.aigcplatformserver.ai.chat;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.entity.TmServerEntity;

public interface IAiService {

    Flux<String> call(TmServerEntity config, String systemMessage, String userMessage);

    Flux<String> stream(TmServerEntity config, String systemMessage, String userMessage);
}

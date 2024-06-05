package space.wenliang.ai.aigcplatformserver.model.chat;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatModelParam;

public interface IAiService {

    Flux<String> call(ChatModelParam param, String systemMessage, String userMessage);

    Flux<String> stream(ChatModelParam param, String systemMessage, String userMessage);
}

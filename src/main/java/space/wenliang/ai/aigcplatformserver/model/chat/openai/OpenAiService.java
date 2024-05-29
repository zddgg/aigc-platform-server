package space.wenliang.ai.aigcplatformserver.model.chat.openai;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.model.chat.AiService;

import java.util.List;

public class OpenAiService implements AiService {

    private final OpenAiChatClient chatClient;

    public OpenAiService(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Flux<String> call(String systemMessage, String userMessage) {
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new SystemMessage(userMessage)));
        return Flux.just(chatClient.call(prompt).getResult().getOutput().getContent());
    }

    @Override
    public Flux<String> stream(String systemMessage, String userMessage) {
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new SystemMessage(userMessage)));
        return chatClient.stream(prompt)
                .mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getContent());
    }
}

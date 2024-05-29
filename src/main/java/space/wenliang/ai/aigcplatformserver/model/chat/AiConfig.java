package space.wenliang.ai.aigcplatformserver.model.chat;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import space.wenliang.ai.aigcplatformserver.model.chat.glm.GlmAiService;
import space.wenliang.ai.aigcplatformserver.model.chat.openai.OpenAiService;
import space.wenliang.ai.aigcplatformserver.model.chat.qwen.QwenAiService;
import space.wenliang.ai.aigcplatformserver.model.chat.spark.SparkAiService;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ai", name = {"type"}, havingValue = "openai", matchIfMissing = true)
    public AiService openAiService(OpenAiChatClient chatClient) {
        return new OpenAiService(chatClient);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ai", name = {"type"}, havingValue = "qwen")
    public AiService qwenAiService() {
        return new QwenAiService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "ai", name = {"type"}, havingValue = "spark")
    public AiService sparkAiService() {
        return new SparkAiService(new ReactorNettyWebSocketClient());
    }

    @Bean
    @ConditionalOnProperty(prefix = "ai", name = {"type"}, havingValue = "glm")
    public AiService glmAiService(WebClient webClient) {
        return new GlmAiService(webClient);
    }
}

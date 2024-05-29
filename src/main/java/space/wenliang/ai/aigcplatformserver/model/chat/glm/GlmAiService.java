package space.wenliang.ai.aigcplatformserver.model.chat.glm;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.model.chat.AiService;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class GlmAiService implements AiService {

    @Value("${ai.glm.api-key:}")
    private String apiKey;

    @Value("${ai.glm.model}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private Float temperature;

    @Value("${spring.ai.openai.chat.options.max-tokens}")
    private Integer maxTokens;

    private final WebClient webClient;

    public GlmAiService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<String> call(String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", model);

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        return webClient.post()
                .uri("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(0);
    }

    @Override
    public Flux<String> stream(String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", model);
        request.put("stream", true);
        request.put("temperature", temperature);
        request.put("max_tokens", maxTokens);

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        return webClient.post()
                .uri("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(s -> Objects.nonNull(s) && !StringUtils.equals(s, "[DONE]"))
                .mapNotNull(s -> JSON.parseObject(s, GlmResponseBody.class))
                .filter(glmResponseBody -> Objects.isNull(glmResponseBody.getChoices().getFirst().getFinish_reason()))
                .mapNotNull(glmResponseBody -> glmResponseBody.getChoices().getFirst().getDelta().getContent())
                .retry(0)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}

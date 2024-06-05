package space.wenliang.ai.aigcplatformserver.model.chat.openAi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatModelParam;
import space.wenliang.ai.aigcplatformserver.model.chat.IAiService;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service("OpenAi")
public class OpenAiService implements IAiService {

    private final WebClient webClient;

    public OpenAiService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<String> call(ChatModelParam param, String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", param.getModel());
        request.put("temperature", param.getTemperature());
        request.put("max_tokens", param.getMaxTokens());

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        return webClient.post()
                .uri(param.getHost() + param.getPath())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + param.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(0);
    }

    @Override
    public Flux<String> stream(ChatModelParam param, String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", param.getModel());
        request.put("stream", true);
        request.put("temperature", param.getTemperature());
        request.put("max_tokens", param.getMaxTokens());

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        return webClient.post()
                .uri(param.getHost() + param.getPath())
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + param.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(s -> Objects.nonNull(s) && !StringUtils.equals(s, "[DONE]"))
                .mapNotNull(s -> JSON.parseObject(s, OpenAiResponseBody.class))
                .filter(openAiResponseBody -> Objects.isNull(openAiResponseBody.getChoices().getFirst().getFinish_reason()))
                .mapNotNull(openAiResponseBody -> openAiResponseBody.getChoices().getFirst().getDelta().getContent())
                .retry(0)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}

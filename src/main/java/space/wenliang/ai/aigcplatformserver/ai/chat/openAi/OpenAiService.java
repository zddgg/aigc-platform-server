package space.wenliang.ai.aigcplatformserver.ai.chat.openAi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.wenliang.ai.aigcplatformserver.ai.chat.IAiService;
import space.wenliang.ai.aigcplatformserver.entity.TmServerEntity;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("OpenAi")
public class OpenAiService implements IAiService {

    private final WebClient webClient;
    private final GlobalWebSocketHandler globalWebSocketHandler;

    public OpenAiService(WebClient webClient, GlobalWebSocketHandler globalWebSocketHandler) {
        this.webClient = webClient;
        this.globalWebSocketHandler = globalWebSocketHandler;
    }

    @Override
    public Flux<String> call(TmServerEntity config, String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", config.getModel());
        request.put("temperature", config.getTemperature());

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        computedMaxTokens(config, request);

        return webClient.post()
                .uri(config.getHost() + config.getPath())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(0);
    }

    @Override
    public Flux<String> stream(TmServerEntity config, String systemMessage, String userMessage) {

        JSONObject request = new JSONObject();
        request.put("model", config.getModel());
        request.put("stream", true);
        request.put("temperature", config.getTemperature());

        JSONArray messages = new JSONArray();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        computedMaxTokens(config, request);

        return webClient.post()
                .uri(config.getHost() + config.getPath())
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getApiKey())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    globalWebSocketHandler.sendErrorMessage(errorBody);
                                    return Mono.error(new RuntimeException(errorBody));
                                })
                )
                .bodyToFlux(String.class)
                .filter(s -> Objects.nonNull(s) && !StringUtils.equals(s, "[DONE]"))
                .mapNotNull(s -> JSON.parseObject(s, OpenAiResponseBody.class))
                .filter(openAiResponseBody -> Objects.isNull(openAiResponseBody.getChoices().getFirst().getFinish_reason()))
                .mapNotNull(openAiResponseBody -> openAiResponseBody.getChoices().getFirst().getDelta().getContent())
                .retry(0)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    public void computedMaxTokens(TmServerEntity config, JSONObject request) {

        if (config.getModel().startsWith("moonshot") && config.getMaxTokens() == 0) {
            Integer total_tokens = webClient.post()
                    .uri(config.getHost() + "/v1/tokenizers/estimate-token-count")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getApiKey())
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,  // 检查状态码是否为错误
                            clientResponse -> clientResponse.bodyToMono(String.class) // 提取错误响应的body
                                    .flatMap(errorBody -> {
                                        globalWebSocketHandler.sendErrorMessage(errorBody);
                                        return Mono.error(new RuntimeException(errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .mapNotNull(JSON::parseObject)
                    .mapNotNull(jsonObject -> jsonObject.getJSONObject("data").getInteger("total_tokens"))
                    .block();

            int modelSize = Integer.parseInt(config.getModel().substring(config.getModel().lastIndexOf("-") + 1).replace("k", ""));

            request.put("max_tokens", (modelSize * 1024) - Optional.ofNullable(total_tokens).orElse(0));
        } else {
            request.put("max_tokens", config.getMaxTokens());
        }
    }
}

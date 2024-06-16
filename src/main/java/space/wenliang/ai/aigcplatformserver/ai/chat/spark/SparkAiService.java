package space.wenliang.ai.aigcplatformserver.ai.chat.spark;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import space.wenliang.ai.aigcplatformserver.ai.chat.IAiService;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service("Spark")
public class SparkAiService implements IAiService {

    private final WebSocketClient webSocketClient;

    public SparkAiService(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public Flux<String> call(ChatModelConfigEntity config, String systemMessage, String userMessage) {
        String authUrl = getAuthUrl(config);

        StringBuilder output = new StringBuilder();

        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        String request = buildRequest(config, systemMessage, userMessage);

        webSocketClient.execute(URI.create(authUrl), session ->
                session.send(Mono.just(session.textMessage(request)))
                        .thenMany(session
                                .receive()
                                .mapNotNull(message -> JSON.parseObject(message.getPayloadAsText(), SparkResponseBody.class)))
                        .doOnNext(response -> {
                            if (response.getHeader().getStatus() == 2) {
                                output.append(response.getPayload().getChoices().getText().getFirst().getContent());
                                sink.tryEmitNext(output.toString());
                            } else {
                                output.append(response.getPayload().getChoices().getText().getFirst().getContent());
                            }
                        })
                        .doFinally(signalType -> {
                            if (signalType.equals(SignalType.ON_COMPLETE) || signalType.equals(SignalType.ON_ERROR)) {
                                session.close();
                                sink.tryEmitComplete();
                            }
                        })
                        .then()).subscribe();
        return sink.asFlux();
    }

    @Override
    public Flux<String> stream(ChatModelConfigEntity config, String systemMessage, String userMessage) {
        String authUrl = getAuthUrl(config);

        String request = buildRequest(config, systemMessage, userMessage);
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");

        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        webSocketClient.execute(
                URI.create(url),
                session -> session.send(Mono.just(session.textMessage(request)))
                        .thenMany(session
                                .receive()
                                .mapNotNull(message -> JSON.parseObject(message.getPayloadAsText(), SparkResponseBody.class)))
                        .doOnNext(response -> {
                            sink.tryEmitNext(response.getPayload().getChoices().getText().getFirst().getContent());
                            //判断是否完成
                            if (response.getHeader().getStatus() == 2) {
                                sink.tryEmitComplete();
                            }
                        })
                        .doFinally(signalType -> {
                            if (signalType.equals(SignalType.ON_ERROR)) {
                                sink.tryEmitComplete();
                                session.close();
                            }
                        })
                        .then()).subscribe();
        return sink.asFlux();
    }

    private String buildRequest(ChatModelConfigEntity config, String systemMessage, String userMessage) {
        JSONObject requestJson = new JSONObject();

        JSONObject header = new JSONObject();  // header参数
        header.put("app_id", config.getAppId());
        header.put("uid", UUID.randomUUID().toString().substring(0, 10));

        JSONObject parameter = new JSONObject(); // parameter参数
        JSONObject chat = new JSONObject();
        chat.put("domain", config.getModel());
        chat.put("temperature", config.getTemperature());
        chat.put("max_tokens", config.getMaxTokens());
        parameter.put("chat", chat);

        JSONObject payload = new JSONObject(); // payload参数
        JSONObject message = new JSONObject();
        JSONArray text = new JSONArray();

        text.add(JSON.toJSON(Map.of("role", "system", "content", systemMessage)));
        text.add(JSON.toJSON(Map.of("role", "user", "content", userMessage)));

        message.put("text", text);
        payload.put("message", message);

        requestJson.put("header", header);
        requestJson.put("parameter", parameter);
        requestJson.put("payload", payload);
        return requestJson.toJSONString();
    }

    @SneakyThrows
    public String getAuthUrl(ChatModelConfigEntity config) {

        String apiKey = config.getApiKey();
        String apiSecret = config.getApiSecret();
        String hostUrl = config.getHost() + config.getPath();

        URI uri = new URI(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + uri.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + uri.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + uri.getHost() + uri.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", uri.getHost()).//
                build();

        return httpUrl.toString();
    }
}

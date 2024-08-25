package space.wenliang.ai.aigcplatformserver.socket;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GlobalWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        sessions.values().remove(session);
        log.info("Connection closed with session id: {}", session.getId());
    }

    public void sendErrorMessage(String title, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "message");
        jsonObject.put("state", "error");
        jsonObject.put("title", title);
        jsonObject.put("message", message);
        for (WebSocketSession session : sessions.values()) {
            try {
                session.sendMessage(new TextMessage(jsonObject.toJSONString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendSuccessMessage(String title, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "message");
        jsonObject.put("state", "success");
        jsonObject.put("title", title);
        jsonObject.put("message", message);
        for (WebSocketSession session : sessions.values()) {
            try {
                session.sendMessage(new TextMessage(jsonObject.toJSONString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendEvent(String event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "event");
        jsonObject.put("event", event);
        for (WebSocketSession session : sessions.values()) {
            try {
                session.sendMessage(new TextMessage(jsonObject.toJSONString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

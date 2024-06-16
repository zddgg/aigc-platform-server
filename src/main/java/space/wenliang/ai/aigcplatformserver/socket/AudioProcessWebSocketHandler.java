package space.wenliang.ai.aigcplatformserver.socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AudioProcessWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, String> params = getQueryParams(Objects.requireNonNull(session.getUri()).getQuery());
        String projectId = params.get("projectId");
        if (StringUtils.isNotBlank(projectId)) {
            sessions.put(projectId, session);
            log.info("Connection established with session id: {} for project: {}", session.getId(), projectId);
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        sessions.values().remove(session);
        log.info("Connection closed with session id: {}", session.getId());
    }

    public void sendMessageToProject(String project, String message) throws Exception {
        WebSocketSession session = sessions.get(project);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            log.warn("No open session found for user: {}", project);
        }
    }

    private Map<String, String> getQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}

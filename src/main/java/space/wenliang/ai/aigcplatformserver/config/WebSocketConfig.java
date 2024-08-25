package space.wenliang.ai.aigcplatformserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.TextProjectWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final TextProjectWebSocketHandler textProjectWebSocketHandler;

    public WebSocketConfig(GlobalWebSocketHandler globalWebSocketHandler,
                           TextProjectWebSocketHandler textProjectWebSocketHandler) {
        this.globalWebSocketHandler = globalWebSocketHandler;
        this.textProjectWebSocketHandler = textProjectWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(globalWebSocketHandler, "/ws/global").setAllowedOrigins("*");
        registry.addHandler(textProjectWebSocketHandler, "/ws/text").setAllowedOrigins("*");
    }
}
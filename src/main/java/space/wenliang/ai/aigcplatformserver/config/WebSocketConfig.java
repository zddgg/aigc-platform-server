package space.wenliang.ai.aigcplatformserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import space.wenliang.ai.aigcplatformserver.socket.AudioProcessWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final AudioProcessWebSocketHandler audioProcessWebSocketHandler;

    public WebSocketConfig(GlobalWebSocketHandler globalWebSocketHandler,
                           AudioProcessWebSocketHandler audioProcessWebSocketHandler) {
        this.globalWebSocketHandler = globalWebSocketHandler;
        this.audioProcessWebSocketHandler = audioProcessWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(globalWebSocketHandler, "/ws/global").setAllowedOrigins("*");
        registry.addHandler(audioProcessWebSocketHandler, "/ws/text").setAllowedOrigins("*");
    }
}
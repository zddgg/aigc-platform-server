package space.wenliang.ai.aigcplatformserver.hooks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownHook implements ApplicationListener<ContextClosedEvent> {

    private final List<ShutdownHook.ShutdownHookListener> shutdownHookListeners;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        for (ShutdownHookListener listener : shutdownHookListeners) {
            try {
                listener.shutdownHook();
            } catch (Exception e) {
                log.error("Shutdown Exception, ClassName: {}", listener.getClass().getName(), e);
            }
        }
    }

    public interface ShutdownHookListener {
        void shutdownHook() throws Exception;
    }
}

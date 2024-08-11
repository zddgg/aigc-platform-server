package space.wenliang.ai.aigcplatformserver.hooks;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHook implements ApplicationRunner {

    private final List<StartHookListener> startHookListeners;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (StartHookListener listener : startHookListeners) {
            try {
                listener.startHook();
            } catch (Exception e) {
                log.error("Start Exception, ClassName: {}", listener.getClass().getName(), e);
            }
        }
    }

    public interface StartHookListener {
        void startHook() throws Exception;
    }
}

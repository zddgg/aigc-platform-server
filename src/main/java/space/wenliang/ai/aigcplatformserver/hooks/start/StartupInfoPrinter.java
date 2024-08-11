package space.wenliang.ai.aigcplatformserver.hooks.start;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;

@Component
@RequiredArgsConstructor
public class StartupInfoPrinter implements StartHook.StartHookListener {

    private final EnvConfig envConfig;

    @Override
    public void startHook() {
        String[] messages = {
                "AIGC Platform Server Startup Information",
                String.format("Version: %s", envConfig.getApplicationVersion()),
                String.format("Console URL: http://127.0.0.1:%s", envConfig.getPort())
        };

        // Calculate the length of the longest message
        int maxLength = 0;
        for (String message : messages) {
            if (message.length() > maxLength) {
                maxLength = message.length();
            }
        }

        // Create the border
        String border = new String(new char[maxLength + 4]).replace("\0", "*");

        // Print the messages inside the box
        System.out.println(border);
        for (String message : messages) {
            System.out.println("* " + padRight(message, maxLength) + " *");
        }
        System.out.println(border);
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}

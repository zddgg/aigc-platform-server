package space.wenliang.ai.aigcplatformserver;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import space.wenliang.ai.aigcplatformserver.config.ApplicationConfig;

@Component
public class StartupInfoPrinter implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationConfig applicationConfig;

    public StartupInfoPrinter(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        printStartupInfo(event);
    }

    private void printStartupInfo(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        String port = context.getEnvironment().getProperty("server.port");

        String[] messages = {
                "AIGC Platform Server Startup Information",
                String.format("Version: %s", applicationConfig.getVersion()),
                String.format("Console URL: http://127.0.0.1:%s", port)
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

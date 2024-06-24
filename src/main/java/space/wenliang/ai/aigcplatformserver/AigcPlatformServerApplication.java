package space.wenliang.ai.aigcplatformserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AigcPlatformServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AigcPlatformServerApplication.class, args);
        System.out.println();
        System.out.println(STR."启动成功: http://127.0.0.1:\{context.getEnvironment().getProperty("server.port")}");
    }

}

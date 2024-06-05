package space.wenliang.ai.aigcplatformserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AigcPlatformServerApplication {

    public static void main(String[] args) {
        System.setProperty("rm_model_dir", "/model");
        SpringApplication.run(AigcPlatformServerApplication.class, args);
    }

}

package space.wenliang.ai.aigcplatformserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableEncryptableProperties
public class AigcPlatformServerApplication {

    public static void main(String[] args) {
        System.setProperty("rm_model_dir", "/model");
        ApplicationContext context = SpringApplication.run(AigcPlatformServerApplication.class, args);
        System.out.println();
        System.out.println(STR."启动成功: http://127.0.0.1:\{context.getEnvironment().getProperty("server.port")}");
    }

}

package space.wenliang.ai.aigcplatformserver;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AigcPlatformServerApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword("123");
        String admin = basicTextEncryptor.encrypt("password");
        System.out.println(admin);
        System.out.println(basicTextEncryptor.decrypt(admin));
    }
}

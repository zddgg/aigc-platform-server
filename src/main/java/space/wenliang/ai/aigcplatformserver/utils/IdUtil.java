package space.wenliang.ai.aigcplatformserver.utils;

import java.util.UUID;

public class IdUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

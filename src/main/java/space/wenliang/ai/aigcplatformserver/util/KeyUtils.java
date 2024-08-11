package space.wenliang.ai.aigcplatformserver.util;

public class KeyUtils {

    public static String combineKey(String... keys) {
        return String.join("-", keys);
    }
}

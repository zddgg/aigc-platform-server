package space.wenliang.ai.aigcplatformserver.util;

public class UnicodeUtils {

    public static String unicodeToChar(String unicode) {
        if (unicode == null) {
            return null;
        }
        if (unicode.startsWith("U+")) {
            unicode = unicode.substring(2).toUpperCase();
        }

        try {
            int codePoint = Integer.parseInt(unicode, 16);
            return new String(Character.toChars(codePoint));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("输入的Unicode编码格式不正确。");
        }
    }
}

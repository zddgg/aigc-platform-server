package space.wenliang.ai.aigcplatformserver.util;

import java.nio.file.Path;

public class PathWrapperUtils {

    public static String getAbsolutePath(Path path, String platform) {
        // 检查输入路径是否为null
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        // 检查平台是否为null
        if (platform == null || platform.isEmpty()) {
            return path.toAbsolutePath().toString();
        }

        // 转换路径为绝对路径
        Path absolutePath = path.toAbsolutePath();

        // 获取绝对路径字符串
        String absolutePathStr = absolutePath.toString();

        // 处理不同平台的路径格式
        if (platform.equalsIgnoreCase("Windows")) {
            // Windows平台的路径格式
            return absolutePathStr.replace("/", "\\");
        } else if (platform.equalsIgnoreCase("Unix")) {
            // 去掉Windows路径中的驱动器号（如 C:）
            if (absolutePathStr.length() > 2 && absolutePathStr.charAt(1) == ':') {
                absolutePathStr = absolutePathStr.substring(2);
            }
            // Unix/Linux平台的路径格式
            return absolutePathStr.replace("\\", "/");
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }
}

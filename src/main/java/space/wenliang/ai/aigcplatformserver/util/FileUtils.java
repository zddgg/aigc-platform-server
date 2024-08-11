package space.wenliang.ai.aigcplatformserver.util;

import com.alibaba.fastjson2.JSON;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    public static void deleteDirectoryAll(Path path) throws IOException {
        if (Files.exists(path)) {
            // 使用Files.walkFileTree来遍历文件夹中的所有文件和目录
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 删除文件
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // 如果访问文件失败，则抛出异常
                    throw exc;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    // 所有文件已被删除，现在可以删除空目录
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // 访问目录失败，则抛出异常
                        throw exc;
                    }
                }
            });
        }
    }

    public static Charset detectCharset(byte[] bytes) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            UniversalDetector detector = new UniversalDetector(null);
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                detector.handleData(buffer, 0, bytesRead);
            }
            detector.dataEnd();
            String detectedCharset = detector.getDetectedCharset();

            return detectedCharset == null ? Charset.defaultCharset() : Charset.forName(detectedCharset);
        }
    }

    public static String fileNameFormat(String fileName) {
        return fileName
                .replace("\\", "")
                .replace("/", "")
                .replace(":", "：")
                .replace("*", "")
                .replace("?", "？")
                .replace("\"", "")
                .replace("<", "")
                .replace(">", "")
                .replace("|", "")
                .replace("--", "-")
                ;
    }

    public static <T> T getObjectFromFile(Path path, Class<T> aClass) {
        try {
            if (Files.exists(path)) {
                return JSON.parseObject(Optional.ofNullable(Files.readString(path)).orElse("[]"), aClass);
            }
            return aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getListFromFile(Path path, Class<T> aClass) {
        try {
            if (Files.exists(path)) {
                return JSON.parseArray(Optional.ofNullable(Files.readString(path)).orElse("[]"), aClass);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFolder(Path path) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (Files.notExists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException("路径无效或不是目录：" + path);
        }

        ProcessBuilder processBuilder = null;

        if (os.contains("win")) {
            processBuilder = new ProcessBuilder("explorer", path.toAbsolutePath().toString());
        } else if (os.contains("mac")) {
            processBuilder = new ProcessBuilder("open", path.toAbsolutePath().toString());
        } else if (os.contains("nix") || os.contains("nux")) {
            processBuilder = new ProcessBuilder("xdg-open", path.toAbsolutePath().toString());
        } else {
            throw new UnsupportedOperationException("不支持的操作系统：" + os);
        }

        processBuilder.start();
    }
}

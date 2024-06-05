package space.wenliang.ai.aigcplatformserver.utils;

import com.alibaba.fastjson2.JSON;

import java.io.IOException;
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
}

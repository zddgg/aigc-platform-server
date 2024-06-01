package space.wenliang.ai.aigcplatformserver.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class AudioUtils {

    public static void mp3ToWav(String input, String output) throws Exception {
        Path outputPath = Path.of(output);

        Files.createDirectories(outputPath.getParent());
        Files.deleteIfExists(outputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", input,
                "-acodec", "pcm_s16le",
                "-ar", "32000",
                "-ac", "1",
                output
        );
        Process process = processBuilder.start();
        process.waitFor();
    }
}
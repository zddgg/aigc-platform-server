package space.wenliang.ai.aigcplatformserver.util;

import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SubtitleUtils {

    public static void srtFile(List<ChapterInfoEntity> chapterInfos, Path outputFilePath) throws IOException {
        long startTime = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chapterInfos.size(); i++) {
            ChapterInfoEntity chapterInfo = chapterInfos.get(i);

            Long lengthInMs = chapterInfo.getAudioLength();
            Integer interval = chapterInfo.getNextAudioInterval();

            long endTime = startTime + lengthInMs;
            if (i != chapterInfos.size() - 1) {
                endTime += interval;
            }
            String startTimeStr = formatTime(startTime);
            String endTimeStr = formatTime(endTime);

            builder.append((i + 1)).append("\n");
            builder.append(startTimeStr).append(" --> ").append(endTimeStr).append("\n");
            builder.append(chapterInfo.getText()).append("\n");
            builder.append("\n");

            startTime = endTime;
        }
        Files.writeString(outputFilePath, builder.toString());
    }

    private static String formatTime(long timeInMilliseconds) {
        int hours = (int) (timeInMilliseconds / 3600000);
        int minutes = (int) ((timeInMilliseconds % 3600000) / 60000);
        int seconds = (int) ((timeInMilliseconds % 60000) / 1000);
        int milliseconds = (int) (timeInMilliseconds % 1000);

        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);
    }
}

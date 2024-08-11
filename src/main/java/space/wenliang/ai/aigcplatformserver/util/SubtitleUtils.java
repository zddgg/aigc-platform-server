package space.wenliang.ai.aigcplatformserver.util;

import space.wenliang.ai.aigcplatformserver.bean.AudioSegment;
import space.wenliang.ai.aigcplatformserver.bean.Subtitle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubtitleUtils {

    private static final int MAX_CHARS_PER_LINE = 15;
    private static final int MIN_CHARS_PER_LINE = MAX_CHARS_PER_LINE / 3;
    private static final List<Character> PUNCTUATION = List.of('，', '。', '！', '？', '；', '：', ',', '.', '!', '?', ';', ':');
    private static final Set<Character> punctuationSet = new HashSet<>(PUNCTUATION);

    public static void srtFile(List<AudioSegment> audioSegments, Path outputFilePath) throws IOException {

        long startTime = 0;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < audioSegments.size(); i++) {
            AudioSegment audioSegment = audioSegments.get(i);

            Long lengthInMs = audioSegment.getAudioLength();
            Integer interval = audioSegment.getAudioInterval();

            long endTime = startTime + lengthInMs;

            String startTimeStr = formatTime(startTime);
            String endTimeStr = formatTime(endTime);

            builder.append((i + 1)).append("\n");
            builder.append(startTimeStr).append(" --> ").append(endTimeStr).append("\n");
            builder.append(replacePunctuations(audioSegment.getText())).append("\n");
            builder.append("\n");

            startTime = endTime + interval;
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

    public static List<String> subtitleSplit(String text, boolean enable) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        if (!enable) {
            return List.of(text);
        }

        int start = 0;
        while (start < text.length()) {
            int end = findSplitPoint(text, start, MAX_CHARS_PER_LINE);
            String subtitle = text.substring(start, end).trim();

            result.add(subtitle);
            start = end;
        }

        return result;
    }

    private static int findSplitPoint(String text, int start, int maxLength) {
        int end = start + maxLength;
        if (end >= text.length()) {
            return text.length();
        }

        for (int i = end; i > start; i--) {
            if (PUNCTUATION.contains(text.charAt(i))) {
                return i + 1;
            }
        }

        for (int i = end; i < text.length(); i++) {
            if (PUNCTUATION.contains(text.charAt(i))) {
                return i + 1;
            }
        }

        return text.length();
    }

    public static String replacePunctuations(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (punctuationSet.contains(c)) {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static List<Subtitle> readSrtFile(Path filePath) throws IOException {
        List<Subtitle> subtitles = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath.toAbsolutePath().toString()));

        String line;
        while ((line = reader.readLine()) != null) {
            // Skip the index line
            line = reader.readLine();
            if (line == null) break;

            // Process the time line
            String[] timeParts = line.split(" --> ");
            if (timeParts.length != 2) break;

            double startTime = parseTimeToSeconds(timeParts[0]);
            double endTime = parseTimeToSeconds(timeParts[1]);

            // Collect text lines
            StringBuilder textBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                textBuilder.append(line).append(" ");
            }
            String text = textBuilder.toString().trim();

            // Add subtitle to list
            subtitles.add(new Subtitle(startTime, endTime, text));
        }

        reader.close();
        return subtitles;
    }

    private static double parseTimeToSeconds(String time) {
        String[] parts = time.split(":|,");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = Integer.parseInt(parts[3]);

        return hours * 3600 + minutes * 60 + seconds + milliseconds / 1000.0;
    }
}

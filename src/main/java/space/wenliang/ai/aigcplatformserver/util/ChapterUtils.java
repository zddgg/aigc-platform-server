package space.wenliang.ai.aigcplatformserver.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChapterUtils {

    private static final int MAX_CHARS_PER_LINE = 37;
    private static final int MIN_CHARS_PER_LINE = MAX_CHARS_PER_LINE / 3;
    private static final String PUNCTUATION = "，。！？；：,.!?;:";

    public static List<Tuple2<String, String>> chapterSplit(byte[] bytes, String chapterTitlePattern) throws IOException {
        List<Tuple2<String, String>> chapters = new ArrayList<>();
        StringBuilder preface = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
            String line;
            StringBuilder chapterContent = new StringBuilder();

            String title = null;
            String content;

            while ((line = reader.readLine()) != null) {

                if (isChapterTitle(line, chapterTitlePattern)) {
                    if (title != null) {
                        content = chapterContent.toString();
                        chapters.add(Tuple.of(title, content));
                        chapterContent.setLength(0); // Resetting the content for the next chapterParse
                    }

                    title = FileUtils.fileNameFormat(line.trim());
                }
                if (title == null) {
                    // If content is null, it means this part is before the first chapterParse
                    preface.append(line.trim()).append("\n");
                } else {
                    // Append line to chapter content if it's not a title
                    chapterContent.append(line.trim()).append("\n");
                }
            }

            if (title != null) {
                content = chapterContent.toString();
                chapters.add(Tuple.of(title, content));
            }
        }

        if (StringUtils.isNotBlank(preface.toString())) {

            String title = FileUtils.fileNameFormat(preface.toString().split("\\n")[0].trim());
            String content = preface.toString();
            chapters.addFirst(Tuple.of(title, content));
        }

        return chapters;
    }

    public static boolean isChapterTitle(String line, String chapterTitlePattern) {
        if (StringUtils.isBlank(chapterTitlePattern)) {
            return false;
        }
        return line.matches(chapterTitlePattern);
    }

    public static List<Tuple2<Boolean, List<String>>> dialogueSplit(String line, List<String> linesModifiers) {
        List<Tuple2<Boolean, List<String>>> sentences = new ArrayList<>();

        if (CollectionUtils.isEmpty(linesModifiers)) {
            return List.of(Tuple.of(false, List.of(line)));
        }

        Matcher matcher = buildModifiersPatternStr(linesModifiers).matcher(line);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String text = line.substring(lastIndex, matcher.start()).trim();
                List<String> strings = textLenFormat(text);
                sentences.add(Tuple.of(false, strings));
            }

            String text = line.substring(matcher.start(), matcher.end()).trim();
            List<String> strings = textLenFormat(text.substring(1, text.length() - 1));
            sentences.add(Tuple.of(true, strings));

//            sentences.add(Tuple.of(line.substring(matcher.start(), matcher.end()).trim(), true));

            lastIndex = matcher.end();
        }
        if (lastIndex < line.length()) {
            String text = line.substring(lastIndex).trim();
            List<String> strings = textLenFormat(text);
            sentences.add(Tuple.of(false, strings));
        }
        return sentences;
    }

    public static Pattern buildModifiersPatternStr(List<String> strings) {
        String patternStr = strings.stream().map(s -> {
                    if (s != null && s.length() == 2) {
                        String var0 = Pattern.quote(s.substring(0, 1));
                        String var1 = Pattern.quote(s.substring(1, 2));
                        return var0 + ".*?" + var1;
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.joining("|"));
        return Pattern.compile(patternStr);
    }

    public static List<String> textLenFormat(String text) {
        return List.of(text);

//        List<String> result = new ArrayList<>();
//        if (text == null || text.isEmpty()) {
//            return result;
//        }
//
//        int start = 0;
//        while (start < text.length()) {
//            int end = findSplitPoint(text, start, MAX_CHARS_PER_LINE);
//            String subtitle = text.substring(start, end).trim();
//
//            if (!result.isEmpty() && subtitle.length() < MIN_CHARS_PER_LINE) {
//                String lastSubtitle = result.remove(result.size() - 1);
//                subtitle = lastSubtitle + " " + subtitle;
//            }
//
//            result.add(subtitle);
//            start = end;
//        }
//
//        return result;
    }

    private static int findSplitPoint(String text, int start, int maxLength) {
        int end = start + maxLength;
        if (end >= text.length()) {
            return text.length();
        }

        for (int i = end; i > start; i--) {
            if (PUNCTUATION.indexOf(text.charAt(i)) != -1) {
                return i + 1;
            }
        }

        for (int i = end; i < text.length(); i++) {
            if (PUNCTUATION.indexOf(text.charAt(i)) != -1) {
                return i + 1;
            }
        }

        return text.length();
    }
}

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

    public static List<Tuple2<Boolean, String>> dialogueSplit(String line, List<String> linesModifiers) {
        List<Tuple2<Boolean, String>> sentences = new ArrayList<>();

        if (CollectionUtils.isEmpty(linesModifiers)) {
            return List.of(Tuple.of(false, line));
        }

        Matcher matcher = buildModifiersPatternStr(linesModifiers).matcher(line);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String text = line.substring(lastIndex, matcher.start()).trim();
                sentences.add(Tuple.of(false, text));
            }

            String text = line.substring(matcher.start(), matcher.end()).trim();
            sentences.add(Tuple.of(true, text.substring(1, text.length() - 1)));

            lastIndex = matcher.end();
        }
        if (lastIndex < line.length()) {
            String text = line.substring(lastIndex).trim();
            sentences.add(Tuple.of(false, text));
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
}

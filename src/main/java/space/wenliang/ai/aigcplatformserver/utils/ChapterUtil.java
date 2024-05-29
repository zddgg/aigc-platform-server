package space.wenliang.ai.aigcplatformserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.ChapterParse;
import space.wenliang.ai.aigcplatformserver.bean.text.ChapterInfo;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

public class ChapterUtil {

    public static List<ChapterParse> chapterSplit(String filePath, String chapterTitlePattern) throws IOException {
        List<ChapterParse> chapterPars = new ArrayList<>();
        StringBuilder preface = new StringBuilder();

        Charset charset = detectCharset(filePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))) {
            String line;
            StringBuilder chapterContent = new StringBuilder();
            ChapterParse chapterParse = null;

            while ((line = reader.readLine()) != null) {

                if (isChapterTitle(line, chapterTitlePattern)) {
                    if (chapterParse != null) {
                        chapterParse.setContent(chapterContent.toString());
                        chapterPars.add(chapterParse);
                        chapterContent.setLength(0); // Resetting the content for the next chapterParse
                    }

                    chapterParse = new ChapterParse();
                    chapterParse.setTitle(FileUtils.fileNameFormat(line.trim()));
                }
                if (chapterParse == null) {
                    // If content is null, it means this part is before the first chapterParse
                    preface.append(line).append("\n");
                } else {
                    // Append line to chapter content if it's not a title
                    chapterContent.append(line).append("\n");
                }
            }

            if (chapterParse != null) {
                chapterParse.setContent(chapterContent.toString());
                chapterPars.add(chapterParse);
            }
        }

        if (StringUtils.isNotBlank(preface.toString())) {
            ChapterParse prefaceChapter = new ChapterParse();
            prefaceChapter.setTitle(FileUtils.fileNameFormat(preface.toString().split("\\n")[0].trim())); // Setting the title from the first line
            prefaceChapter.setContent(preface.toString());
            prefaceChapter.setPrologue(true);
            chapterPars.addFirst(prefaceChapter);
        }

        return chapterPars;
    }

    public static boolean isChapterTitle(String line, String chapterTitlePattern) {
        if (StringUtils.isBlank(chapterTitlePattern)) {
            return false;
        }
        return line.matches(chapterTitlePattern);
    }

    private static Charset detectCharset(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath);
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

    public static List<ChapterInfo> parseChapterInfo(String chapter, List<String> linesModifiers) {
        List<ChapterInfo> lineInfos = new ArrayList<>();
        String[] split = chapter.split("\n");
        range(0, split.length).forEach(pIndex -> {
            String line = split[pIndex];
            String trimmedLine = line.stripLeading();
            if (!trimmedLine.isEmpty()) {
                List<ChapterInfo> sentenceInfos = parseLineInfo(pIndex, trimmedLine, linesModifiers);
                lineInfos.addAll(sentenceInfos);
            }
        });
        return lineInfos;
    }

    public static List<ChapterInfo> parseLineInfo(Integer pIndex, String line, List<String> linesModifiers) {
        List<ChapterInfo> sentenceInfos = new ArrayList<>();

        int sIndex = 0;
        if (CollectionUtils.isEmpty(linesModifiers)) {
            return List.of(new ChapterInfo(pIndex, sIndex, line));
        }

        Matcher matcher = buildModifiersPatternStr(linesModifiers).matcher(line);
        int lastIndex = 0;
        while (matcher.find()) {
            int start = matcher.start() - 1;
            int end = matcher.end() + 1;
            if (start > lastIndex) {
                sentenceInfos.add(new ChapterInfo(pIndex, sIndex++, line.substring(lastIndex, start)));
            }
            sentenceInfos.add(new ChapterInfo(pIndex, sIndex++, line.substring(start, end), true));
            lastIndex = end;
        }
        if (lastIndex < line.length()) {
            sentenceInfos.add(new ChapterInfo(pIndex, sIndex, line.substring(lastIndex)));
        }
        return sentenceInfos;
    }

    public static Pattern buildModifiersPatternStr(List<String> strings) {
        String patternStr = strings.stream().map(s -> {
                    if (s != null && s.length() == 2) {
                        String[] split = s.split("");
                        String var0 = split[0];
                        String var1 = split[1];
                        return STR."(?<=\\\{var0})[^\\\{var1}]*(?=\\\{var1})";
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.joining("|"));

        return Pattern.compile(patternStr);
    }
}

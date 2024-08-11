package space.wenliang.ai.aigcplatformserver.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PinyinCacheService implements StartHook.StartHookListener {

    public static Map<String, List<String>> uniHanPinyinsMap = new HashMap<>();
    public static Map<String, List<String>> pinyinUniHansMap = new HashMap<>();

    private final EnvConfig envConfig;


    @Override
    public void startHook() throws Exception {
        refreshCache();
    }

    public void refreshCache() {
        buildUniHanPinyinsCache();
        buildPinyinUniHansCache();
    }

    public Map<String, List<String>> getTextPinyins() {
        return uniHanPinyinsMap;
    }

    public String getUniHanByPinyin(String pinyin) {
        if (!CollectionUtils.isEmpty(pinyinUniHansMap) && !CollectionUtils.isEmpty(pinyinUniHansMap.get(pinyin))) {
            return pinyinUniHansMap.get(pinyin).getFirst();
        }
        return null;
    }

    @SneakyThrows
    public void buildUniHanPinyinsCache() {
        Map<String, List<String>> result = new HashMap<>();

        Path path = Path.of(envConfig.getUserDir(), "unihan-pinyin.txt");
        List<String> lines;
        if (Files.exists(path)) {
            lines = Files.readAllLines(path);
        } else {
            File file = ResourceUtils.getFile("classpath:pinyin/unihan-pinyin.txt");
            lines = Files.readAllLines(file.toPath());
        }

        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(":");
            if (parts.length < 2) {
                continue;
            }

            String unicode = parts[0].trim();
            String pinyinPart = parts[1].split("#")[0].trim();
            String[] pinyins = pinyinPart.split(",");

            List<String> pinyinList = new ArrayList<>();
            for (String pinyin : pinyins) {
                pinyinList.add(pinyin.trim());
            }

            result.put(unicode, pinyinList);
        }

        uniHanPinyinsMap = result;
    }

    @SneakyThrows
    public void buildPinyinUniHansCache() {
        Map<String, List<String>> result = new TreeMap<>(new PinyinToneComparator());

        Path path = Path.of(envConfig.getUserDir(), "pinyin-unihan.txt");
        List<String> lines;
        if (Files.exists(path)) {
            lines = Files.readAllLines(path);
        } else {
            File file = ResourceUtils.getFile("classpath:pinyin/pinyin-unihan.txt");
            lines = Files.readAllLines(file.toPath());
        }

        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(":");
            if (parts.length < 2) {
                continue;
            }

            String pinyin = parts[0].trim();
            String[] unicodes = parts[1].split(",");

            List<String> pinyinList = new ArrayList<>();
            for (String unicode : unicodes) {
                if (StringUtils.isNotBlank(unicode)) {
                    pinyinList.add(unicode.trim());
                }
            }

            result.put(pinyin, pinyinList);
        }

        pinyinUniHansMap = result;
    }

    public static class PinyinToneComparator implements Comparator<String> {

        // Helper method to remove tone marks and get the base pinyin
        private String removeToneMarks(String pinyin) {
            return pinyin
                    .replaceAll("[āáǎà]", "a")
                    .replaceAll("[ēéěè]", "e")
                    .replaceAll("[īíǐì]", "i")
                    .replaceAll("[ōóǒò]", "o")
                    .replaceAll("[ūúǔù]", "u")
                    .replaceAll("[ǖǘǚǜü]", "ü")
                    .replaceAll("[ńň]", "n")
                    .replaceAll("[ḿ]", "m");
        }

        // Helper method to get the tone number
        private int getToneNumber(String pinyin) {
            if (pinyin.matches(".*[āēīōūǖ].*")) return 1;
            if (pinyin.matches(".*[áéíóúǘ].*")) return 2;
            if (pinyin.matches(".*[ǎěǐǒǔǚ].*")) return 3;
            if (pinyin.matches(".*[àèìòùǜ].*")) return 4;
            return 5; // Represents the neutral tone (轻声)
        }

        @Override
        public int compare(String pinyin1, String pinyin2) {
            String base1 = removeToneMarks(pinyin1);
            String base2 = removeToneMarks(pinyin2);

            int baseCompare = base1.compareTo(base2);
            if (baseCompare != 0) {
                return baseCompare;
            } else {
                return Integer.compare(getToneNumber(pinyin1), getToneNumber(pinyin2));
            }
        }
    }
}

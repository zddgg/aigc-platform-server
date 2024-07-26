package space.wenliang.ai.aigcplatformserver.service.cache.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import space.wenliang.ai.aigcplatformserver.common.CacheConstants;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.service.cache.CacheService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final PathConfig pathConfig;

    @SneakyThrows
    @Cacheable(value = CacheConstants.PINYIN, key = "'text-pinyins'", sync = true)
    @Override
    public Map<String, List<String>> getTextPinyins() {
        Map<String, List<String>> result = new HashMap<>();

        Path path = Path.of(pathConfig.getUserDir(), "pinyin.txt");
        List<String> lines;
        if (Files.exists(path)) {
            lines = Files.readAllLines(path);
        } else {
            File file = ResourceUtils.getFile("classpath:pinyin/pinyin.txt");
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

        return result;
    }

    @SneakyThrows
    @Cacheable(value = CacheConstants.PINYIN, key = "'pinyin-texts'", sync = true)
    @Override
    public Map<String, List<String>> getPinyinTexts() {
        Map<String, List<String>> textPinyins = getTextPinyins();
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : textPinyins.entrySet()) {
            String unicode = entry.getKey();
            List<String> pinyins = entry.getValue();

            for (String pinyin : pinyins) {
                result.computeIfAbsent(pinyin, k -> new ArrayList<>()).add(unicode);
            }
        }

        return result;
    }
}

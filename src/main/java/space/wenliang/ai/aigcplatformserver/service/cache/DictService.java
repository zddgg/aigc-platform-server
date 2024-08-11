package space.wenliang.ai.aigcplatformserver.service.cache;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.LangDict;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.hooks.ShutdownHook;
import space.wenliang.ai.aigcplatformserver.hooks.StartHook;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictService implements StartHook.StartHookListener, ShutdownHook.ShutdownHookListener {

    private static List<LangDict> LANG_DICT_LIST = new ArrayList<>();

    private final EnvConfig envConfig;

    @Override
    public void startHook() {
        Path path = envConfig.buildConfigPath("lang-dict.json");
        if (Files.exists(path)) {
            try {
                LANG_DICT_LIST = FileUtils.getListFromFile(path, LangDict.class);
            } catch (Exception e) {
                log.error("读取[lang-dict.json]失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void shutdownHook() throws IOException {
        Path path = envConfig.buildConfigPath("lang-dict.json");
        Files.write(path, JSON.toJSONBytes(LANG_DICT_LIST));
    }

    public List<LangDict> getLangDict() {
        return LANG_DICT_LIST;
    }

    public LangDict getLangDict(String enName) {
        return LANG_DICT_LIST.stream().filter(v -> StringUtils.equals(v.getEnName(), enName))
                .findAny().orElse(null);
    }

    public void editLangDict(LangDict langDict) {
        for (LangDict dict : LANG_DICT_LIST) {
            if (StringUtils.equals(dict.getEnName(), langDict.getEnName())) {
                dict.setZhName(langDict.getZhName());
                dict.setText(langDict.getText());
            }
        }
    }

    public void langDictSort(List<LangDict> langDicts) {
        List<String> enNames = langDicts.stream().map(LangDict::getEnName).toList();
        LANG_DICT_LIST = LANG_DICT_LIST.stream()
                .sorted(Comparator.comparingInt(v -> enNames.indexOf(v.getEnName())))
                .toList();
    }
}

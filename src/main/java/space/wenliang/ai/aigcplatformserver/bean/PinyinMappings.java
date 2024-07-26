package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class PinyinMappings {
    private List<String> text;

    @Data
    public static class TextPinyin {
        private String text;
        private List<String> pinyins;
    }

    @Data
    public static class PinyinText {
        private String pinyin;
        private List<String> texts;
    }
}

package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class EdgeTtsConfig {

    private List<LangText> langTexts = new ArrayList<>();
    private List<EdgeTtsVoice> voices = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LangText {
        private String enName;
        private String zhName;
        private String text;
        private Boolean show;

        public LangText(String enName) {
            this.enName = enName;
        }
    }
}

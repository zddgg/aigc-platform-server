package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class EdgeTtsVoice {
    private String Name;
    private String ShortName;
    private String Gender;
    private String Locale;
    private String SuggestedCodec;
    private String FriendlyName;
    private String Status;
    private List<VoiceTag> VoiceTag;

    @Data
    public static class VoiceTag {
        private List<String> ContentCategories;
        private List<String> VoicePersonalities;
    }
}

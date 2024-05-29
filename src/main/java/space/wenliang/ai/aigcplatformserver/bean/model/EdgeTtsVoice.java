package space.wenliang.ai.aigcplatformserver.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class EdgeTtsVoice {
    @JsonIgnore
    private String Name;
    private String ShortName;
    private String Gender;
    private String Locale;
    private String SuggestedCodec;
    @JsonIgnore
    private String FriendlyName;

    @JsonIgnore
    private String Status;
    @JsonIgnore
    private List<VoiceTag> VoiceTag;

    private String url;

    @Data
    public static class VoiceTag {
        private List<String> ContentCategories;
        private List<String> VoicePersonalities;
    }
}

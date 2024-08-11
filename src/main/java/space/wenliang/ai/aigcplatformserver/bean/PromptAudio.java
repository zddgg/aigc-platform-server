package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class PromptAudio {

    private String paGroup;
    private String paRole;
    private String paRoleGender;
    private String paRoleAge;
    private String paRoleLang;
    private List<String> paRoleTags;
    private String paRoleAvatar;
    private List<PaMood> paMoods;

    @Data
    public static class PaMood {
        private String paMood;
        private String paMoodAvatar;
        private List<PaAudio> PaAudios;
    }

    @Data
    public static class PaAudio {
        private Integer id;
        private String paId;
        private String paAudio;
        private String paAudioText;
        private String paAudioLang;
        private List<String> paAudioTags;
        private String audioUrl;
    }
}

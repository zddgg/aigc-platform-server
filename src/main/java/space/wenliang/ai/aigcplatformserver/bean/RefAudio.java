package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class RefAudio {

    private String name;
    private String group;
    private String gender;
    private String ageGroup;
    private String language;
    private List<Mood> moods;
    private List<String> tags;
    private String avatarUrl;

    @Data
    public static class Mood {
        private String name;
        private List<MoodAudio> moodAudios;
        private String avatarUrl;
    }

    @Data
    public static class MoodAudio {
        private String id;
        private String name;
        private String text;
        private List<String> tags;
        private String audioUrl;
    }
}

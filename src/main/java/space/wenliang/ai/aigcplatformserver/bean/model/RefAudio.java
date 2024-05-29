package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

import java.util.List;

@Data
public class RefAudio {

    private Integer id;
    private String name;
    private String group;
    private String gender;
    private String ageGroup;
    private String language;
    private List<Mood> moods;
    private List<String> tags;
    private String avatar;

    @Data
    public static class Mood {
        private Integer id;
        private String name;
        private List<MoodAudio> moodAudios;
        private String avatar;
    }

    @Data
    public static class MoodAudio {
        private Integer id;
        private String name;
        private String text;
        private List<String> tags;
        private String url;
    }
}

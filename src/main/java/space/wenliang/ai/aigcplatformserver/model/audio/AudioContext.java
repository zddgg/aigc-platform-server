package space.wenliang.ai.aigcplatformserver.model.audio;

import lombok.Data;

@Data
public class AudioContext {

    private String text;
    private String textLanguage;

    private String type;
    private String url;

    private String refAudioPath;
    private String refText;
    private String refTextLanguage;

    private String speaker;

    private String mediaType;
    private String outputDir;
    private String outputName;
}

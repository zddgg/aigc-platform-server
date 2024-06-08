package space.wenliang.ai.aigcplatformserver.model.audio;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.bean.model.AudioServerConfig;
import space.wenliang.ai.aigcplatformserver.bean.model.ChatTtsConfig;

@Data
public class AudioContext {

    private String text;
    private String textLang;

    private String type;
    private AudioServerConfig audioServerConfig;

    private String modelGroup;
    private String model;

    private String refAudioPath;
    private String refText;
    private String refTextLang;

    private String speaker;

    private String mediaType;
    private String outputDir;
    private String outputName;

    private ChatTtsConfig chatTtsConfig;
}

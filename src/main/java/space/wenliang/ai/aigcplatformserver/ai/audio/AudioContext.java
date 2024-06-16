package space.wenliang.ai.aigcplatformserver.ai.audio;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.*;

@Data
public class AudioContext {

    private String text;
    private String textLang;

    private String type;
    private String modelId;
    private String configId;
    private String refAudioId;

    private String mediaType;
    private String outputDir;
    private String outputName;

    private AudioServerConfigEntity audioServerConfig;

    private RefAudioEntity refAudio;

    private GptSovitsModelEntity gptSovitsModel;
    private GptSovitsConfigEntity gptSovitsConfig;

    private FishSpeechModelEntity fishSpeechModel;
    private FishSpeechConfigEntity fishSpeechConfig;

    private ChatTtsConfigEntity chatTtsConfig;

    private EdgeTtsConfigEntity edgeTtsConfig;
}

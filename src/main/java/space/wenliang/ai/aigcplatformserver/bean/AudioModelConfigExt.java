package space.wenliang.ai.aigcplatformserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.entity.*;

@Getter
@Setter
public class AudioModelConfigExt {

    @TableField(exist = false)
    private GptSovitsModelEntity gptSovitsModel;
    @TableField(exist = false)
    private GptSovitsConfigEntity gptSovitsConfig;

    @TableField(exist = false)
    private FishSpeechModelEntity fishSpeechModel;
    @TableField(exist = false)
    private FishSpeechConfigEntity fishSpeechConfig;

    @TableField(exist = false)
    private ChatTtsConfigEntity chatTtsConfig;

    @TableField(exist = false)
    private EdgeTtsConfigEntity edgeTtsConfig;

    @TableField(exist = false)
    private RefAudioEntity refAudio;

    @TableField(exist = false)
    private String audioUrl;

    @TableField(exist = false)
    private String audioPath;

    @JsonIgnore
    @TableField(exist = false)
    private byte[] audioBytes;
}

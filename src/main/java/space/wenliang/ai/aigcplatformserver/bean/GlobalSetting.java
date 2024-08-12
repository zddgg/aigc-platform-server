package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class GlobalSetting {

    private Boolean subtitleOptimize = true;
    private Integer subAudioInterval = 300;
}

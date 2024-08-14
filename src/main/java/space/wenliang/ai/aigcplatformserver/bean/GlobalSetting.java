package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class GlobalSetting {

    private Boolean subtitleOptimize = false;
    private Integer subAudioInterval = 300;
}

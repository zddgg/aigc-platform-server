package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlsUpdateVO extends Chapter {
    private Boolean enableVolume;
    private Integer volume;
    private Boolean enableSpeed;
    private Double speed;
    private Boolean enableInterval;
    private Integer interval;
}

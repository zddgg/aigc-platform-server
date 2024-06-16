package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlsUpdate {
    private String projectId;
    private String chapterId;

    private Boolean enableVolume;
    private Double volume;
    private Boolean enableSpeed;
    private Double speed;
    private Boolean enableInterval;
    private Integer interval;
}
package space.wenliang.ai.aigcplatformserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AudioSegment {

    private Integer id;
    private Integer part;
    private String audioName;

    private String text;

    private Double audioVolume;
    private Double audioSpeed;
    private Integer audioInterval;

    private Long audioLength;

    @JsonIgnore
    private String audioPath;
    @JsonIgnore
    private byte[] audioBytes;
}
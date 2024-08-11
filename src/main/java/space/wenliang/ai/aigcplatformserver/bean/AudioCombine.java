package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class AudioCombine {

    private Integer id;

    private AudioSegment audioSegment;

    private List<AudioSegment> audioSegments;
}

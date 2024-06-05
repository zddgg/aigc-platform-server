package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EdgeTtsModelConfig extends ModelConfig {

    private String createDate;
    private List<EdgeTtsVoice> voices;
}

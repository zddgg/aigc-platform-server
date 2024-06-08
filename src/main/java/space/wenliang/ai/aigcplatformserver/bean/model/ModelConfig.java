package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

import java.util.List;

@Data
public class ModelConfig {
    private String modelType;
    private List<String> model;
    private List<String> audio;
    private ChatTtsConfig chatTtsConfig;
}

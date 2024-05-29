package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

@Data
public class ChatModelParam {
    private String id;
    private String name;
    private String interfaceType;
    private String host;
    private String apiKey;
    private String model;
    private Float temperature;
    private Integer maxTokens;
    private String templateName;
}

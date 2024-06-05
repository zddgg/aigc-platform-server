package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

@Data
public class ChatModelParam {
    private String id;
    private String name;
    private String interfaceType;
    private String host;
    private String path;
    private String apiKey;
    private String apiSecret;
    private String appId;
    private String model;
    private Float temperature;
    private Integer maxTokens;
    private Boolean active;
    private String templateName;
}

package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class GsvModel {
    private Integer id;
    private String name;
    private String group;
    private String gptWeights;
    private String sovitsWeights;
}

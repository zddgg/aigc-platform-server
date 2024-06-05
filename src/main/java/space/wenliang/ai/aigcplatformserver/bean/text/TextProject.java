package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

@Data
public class TextProject {
    private String project;
    private Integer textNum;
    private Integer RoleNum;
    private String stage;
}

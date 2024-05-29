package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

@Data
public class LinesMapping {
    private String linesIndex;
    private String role;
    private String gender;
    private String ageGroup;
    private String mood;
}
package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

import java.util.List;

@Data
public class Chapter {
    private String project;
    private String chapter;
    private List<String> indexes;
}

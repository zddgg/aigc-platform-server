package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

@Data
public class ChapterSplitVO {
    private String project;
    private String chapterPattern;
    private String linesPattern;
}

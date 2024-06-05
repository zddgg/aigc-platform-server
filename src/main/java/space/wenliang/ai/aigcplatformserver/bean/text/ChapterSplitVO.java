package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterSplitVO extends Chapter {
    private String chapterPattern;
    private String linesPattern;
    private String textContent;
}

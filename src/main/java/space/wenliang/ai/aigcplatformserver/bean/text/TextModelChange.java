package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

@Data
public class TextModelChange {
    private Chapter chapter;
    private ChapterInfo chapterInfo;
    private Boolean loadModel;
}

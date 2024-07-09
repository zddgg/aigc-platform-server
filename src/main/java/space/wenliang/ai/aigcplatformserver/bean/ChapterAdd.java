package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.List;

@Data
public class ChapterAdd {
    private TextChapterEntity textChapter;
    private List<TextChapterEntity> sortChapters;
}

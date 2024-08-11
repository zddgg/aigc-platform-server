package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class ChapterExpose {
    private String projectId;
    private String chapterId;
    private List<Integer> chapterInfoIds;
    private Boolean combineAudio;
    private Boolean subtitle;
}

package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class ChapterExpose {
    private String projectId;
    private String chapterId;
    private List<String> indexes;
    private Boolean combineAudio;
    private Boolean subtitle;
}

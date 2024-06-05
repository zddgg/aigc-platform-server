package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

import java.util.List;

@Data
public class ChapterExpose {
    private Chapter chapter;
    private List<String> indexes;
    private Boolean combineAudio;
    private Boolean subtitle;
}

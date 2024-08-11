package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

@Data
public class FormatTextProject {
    private String projectName;
    private String projectType;
    private List<ChapterInfoEntity> chapterInfos;
}

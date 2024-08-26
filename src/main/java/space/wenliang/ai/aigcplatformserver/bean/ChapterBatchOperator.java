package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class ChapterBatchOperator {
    private String projectId;
    private String chapterId;
    private List<Integer> chapterInfoIds;
    private String operatorType;
    private Boolean booleanValue;
}

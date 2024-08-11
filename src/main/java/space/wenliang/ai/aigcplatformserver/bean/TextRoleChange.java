package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.List;

@Data
public class TextRoleChange {
    private String projectId;
    private String chapterId;
    private List<String> chapterInfoIds;
    private String formRoleName;
    private String fromRoleType;
    private Boolean changeModel;
}

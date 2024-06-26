package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class TextRoleChange {
    private String projectId;
    private String chapterId;
    private String chapterInfoId;
    private String formRoleName;
    private String fromRoleType;
    private Boolean changeModel;
}

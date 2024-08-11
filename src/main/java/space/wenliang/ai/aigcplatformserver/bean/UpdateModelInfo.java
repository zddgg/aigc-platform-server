package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.AudioRoleInfo;

import java.util.List;

@Data
public class UpdateModelInfo extends AudioRoleInfo {
    private String projectId;
    private String chapterId;
    private List<Integer> ids;
}

package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleInferenceEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoleInferenceData {
    private String content;
    private String lines;
    private List<TextRoleInferenceEntity> textRoleMoods = new ArrayList<>();
    private List<AiResult.Role> roles = new ArrayList<>();
}

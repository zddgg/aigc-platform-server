package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

@Data
public class RoleRename {
    private Chapter chapter;
    private String role;
    private String newRole;
    private String roleType;
}

package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.bean.model.ModelSelect;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends ModelSelect {
    private String role;
    private String gender;
    private String ageGroup;
    private Integer roleCount = 0;

    public Role(String role) {
        this.role = role;
        this.gender = "未知";
        this.ageGroup = "未知";
    }

    public void setModelSelect(ModelSelect modelSelect) {
        if (Objects.isNull(modelSelect)) {
            return;
        }
        this.setModelType(modelSelect.getModelType());
        this.setModel(modelSelect.getModel());
        this.setAudio(modelSelect.getAudio());
    }
}
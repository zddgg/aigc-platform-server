package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import space.wenliang.ai.aigcplatformserver.bean.model.ModelConfig;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends ModelConfig {
    private String role;
    private String gender;
    private String ageGroup;
    private Integer roleCount = 0;

    public Role(String role) {
        this.role = role;
        this.gender = "未知";
        this.ageGroup = "未知";
    }

    public void setModelConfig(ModelConfig modelConfig) {
        if (Objects.isNull(modelConfig)) {
            return;
        }
        this.setModelType(modelConfig.getModelType());

        if (StringUtils.equals(modelConfig.getModelType(), ModelTypeEnum.gpt_sovits.getName())
                || StringUtils.equals(modelConfig.getModelType(), ModelTypeEnum.fish_speech.getName())
                || StringUtils.equals(modelConfig.getModelType(), ModelTypeEnum.edge_tts.getName())) {

            this.setModel(modelConfig.getModel());
            this.setAudio(modelConfig.getAudio());

            this.setChatTtsConfig(null);
        }

        if (StringUtils.equals(modelConfig.getModelType(), ModelTypeEnum.chat_tts.getName())) {

            this.setModel(null);
            this.setAudio(null);

            this.setChatTtsConfig(modelConfig.getChatTtsConfig());
        }
    }
}
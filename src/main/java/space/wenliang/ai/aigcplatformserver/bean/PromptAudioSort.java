package space.wenliang.ai.aigcplatformserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptAudioSort {
    private String group;
    private Integer sortOrder;
    private Boolean showFlag;
}
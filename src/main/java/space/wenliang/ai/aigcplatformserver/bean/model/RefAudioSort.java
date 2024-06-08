package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefAudioSort {
    private String group;
    private Integer sortOrder;
    private Boolean showFlag;
}

package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AudioModelInfoKey {

    /**
     *
     */
    @TableField(value = "am_type")
    public String amType;

    /**
     *
     */
    @TableField(value = "am_pa_id")
    public String amPaId;

    /**
     *
     */
    @TableField(value = "am_mf_id")
    public String amMfId;

    /**
     *
     */
    @TableField(value = "am_mc_id")
    public String amMcId;

    /**
     *
     */
    @TableField(value = "am_mc_params_json")
    public String amMcParamsJson;
}

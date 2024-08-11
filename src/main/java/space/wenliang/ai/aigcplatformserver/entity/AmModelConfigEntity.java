package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName am_model_config
 */
@TableName(value = "am_model_config")
@Data
public class AmModelConfigEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     *
     */
    @TableField(value = "mc_id")
    private String mcId;
    /**
     *
     */
    @TableField(value = "mc_name")
    private String mcName;
    /**
     *
     */
    @TableField(value = "mc_params_json")
    private String mcParamsJson;
    /**
     *
     */
    @TableField(value = "am_type")
    private String amType;
    /**
     *
     */
    @TableField(value = "mf_id")
    private String mfId;
    /**
     *
     */
    @TableField(value = "pa_id")
    private String paId;
    /**
     *
     */
    @TableField(value = "text")
    private String text;
    /**
     *
     */
    @TableField(value = "save_audio")
    private Boolean saveAudio;
    /**
     *
     */
    @TableField(value = "show_flag")
    private Boolean showFlag;
}
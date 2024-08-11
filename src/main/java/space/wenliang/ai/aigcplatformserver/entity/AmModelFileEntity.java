package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName am_model_file
 */
@TableName(value = "am_model_file")
@Data
public class AmModelFileEntity implements Serializable {
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
    @TableField(value = "mf_id")
    private String mfId;
    /**
     *
     */
    @TableField(value = "am_type")
    private String amType;
    /**
     *
     */
    @TableField(value = "mf_group")
    private String mfGroup;
    /**
     *
     */
    @TableField(value = "mf_role")
    private String mfRole;
    /**
     *
     */
    @TableField(value = "mf_json")
    private String mfJson;
}
package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName text_role
 */
@TableName(value = "text_role")
@Data
public class TextRoleEntity extends AudioRoleInfo implements Serializable {
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
    @TableField(value = "project_id")
    private String projectId;
    /**
     *
     */
    @TableField(value = "chapter_id")
    private String chapterId;

    @TableField(exist = false)
    private Long roleCount;

    @TableField(exist = false)
    private Boolean coverCommonRole;
}
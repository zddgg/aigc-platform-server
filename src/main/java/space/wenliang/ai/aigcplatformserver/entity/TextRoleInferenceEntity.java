package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName text_role_inference
 */
@TableName(value = "text_role_inference")
@Data
public class TextRoleInferenceEntity implements Serializable {
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
    /**
     *
     */
    @TableField(value = "role")
    private String role;
    /**
     *
     */
    @TableField(value = "gender")
    private String gender;
    /**
     *
     */
    @TableField(value = "age")
    private String age;
    /**
     *
     */
    @TableField(value = "mood")
    private String mood;
    /**
     *
     */
    @TableField(value = "text_index")
    private String textIndex;

    @TableField(exist = false)
    private String text;
}
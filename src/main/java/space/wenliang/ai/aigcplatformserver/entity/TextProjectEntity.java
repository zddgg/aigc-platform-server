package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName text_project
 */
@TableName(value = "text_project")
@Data
public class TextProjectEntity implements Serializable {
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
    @TableField(value = "project_name")
    private String projectName;
    /**
     *
     */
    @TableField(value = "project_type")
    private String projectType;
    /**
     *
     */
    @TableField(value = "content")
    private String content;
    /**
     *
     */
    @TableField(value = "chapter_pattern")
    private String chapterPattern;
}
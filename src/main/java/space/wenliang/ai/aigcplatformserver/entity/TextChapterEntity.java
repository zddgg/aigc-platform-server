package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName text_chapter
 */
@TableName(value = "text_chapter")
@Data
public class TextChapterEntity implements Serializable {
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
    @TableField(value = "chapter_id")
    private String chapterId;
    /**
     *
     */
    @TableField(value = "project_id")
    private String projectId;
    /**
     *
     */
    @TableField(value = "chapter_name")
    private String chapterName;
    /**
     *
     */
    @TableField(value = "content")
    private String content;
    /**
     *
     */
    @TableField(value = "dialogue_pattern")
    private String dialoguePattern;
    /**
     *
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;


    @TableField(exist = false)
    private Integer wordNum;

    @TableField(exist = false)
    private Integer textNum;

    @TableField(exist = false)
    private Integer dialogueNum;

    @TableField(exist = false)
    private Integer roleNum;

    @TableField(exist = false)
    private Integer audioTaskState;
}
package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class TextChapterEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String projectId;

    private String chapterId;

    private String chapterName;

    private String content;

    private String dialoguePattern;

    @TableField(exist = false)
    private Long textNum;

    @TableField(exist = false)
    private Long roleNum;

    @TableField(exist = false)
    private String stage;
}

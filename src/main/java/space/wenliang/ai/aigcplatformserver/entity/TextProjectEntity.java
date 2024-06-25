package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class TextProjectEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String projectId;

    private String projectName;

    private String projectType;

    private String content;

    private String chapterPattern;
}

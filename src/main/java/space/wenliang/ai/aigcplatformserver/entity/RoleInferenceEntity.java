package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class RoleInferenceEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String projectId;

    private String chapterId;

    private String textIndex;

    private String role;

    private String gender;

    private String ageGroup;

    private String mood;

}

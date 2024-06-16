package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.bean.AudioModelConfigExt;

@Getter
@Setter
@TableName
public class TextCommonRoleEntity extends AudioModelConfigExt {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String projectId;

    private String role;

    private String gender;

    private String ageGroup;

    private String audioModelType;
    private String audioModelId;
    private String audioConfigId;
    private String refAudioId;
}
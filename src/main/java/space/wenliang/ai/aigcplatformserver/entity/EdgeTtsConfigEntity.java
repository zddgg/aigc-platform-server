package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class EdgeTtsConfigEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String configId;

    private String name;
    private String shortName;
    private String gender;
    private String locale;
    private String suggestedCodec;
    private String friendlyName;
    private String status;
    private String voiceTag;

    @TableField(exist = false)
    private String text;
}

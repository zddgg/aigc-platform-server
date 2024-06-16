package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class GptSovitsModelEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String modelId;
    private String modelGroup;
    private String modelName;
    private String ckpt;
    private String pth;
}

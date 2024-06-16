package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class EdgeTtsSettingEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String enName;
    private String zhName;
    private String text;
    private Boolean showFlag;
}

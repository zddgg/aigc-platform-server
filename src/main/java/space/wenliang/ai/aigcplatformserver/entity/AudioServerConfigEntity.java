package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class AudioServerConfigEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String host;
    private String path;
    private String apiVersion;
}

package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class ChatModelTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String templateName;

    private String interfaceType;
    private String host;
    private String path;

    private String apiKey;

    private String model;
    private Float temperature;
    private Integer maxTokens;

    private String appId;
    private String apiSecret;
}

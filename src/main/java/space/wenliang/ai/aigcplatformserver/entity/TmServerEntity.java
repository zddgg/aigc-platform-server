package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName tm_server
 */
@TableName(value = "tm_server")
@Data
public class TmServerEntity implements Serializable {
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
    @TableField(value = "name")
    private String name;
    /**
     *
     */
    @TableField(value = "interface_type")
    private String interfaceType;
    /**
     *
     */
    @TableField(value = "host")
    private String host;
    /**
     *
     */
    @TableField(value = "path")
    private String path;
    /**
     *
     */
    @TableField(value = "api_key")
    private String apiKey;
    /**
     *
     */
    @TableField(value = "model")
    private String model;
    /**
     *
     */
    @TableField(value = "temperature")
    private Float temperature;
    /**
     *
     */
    @TableField(value = "max_tokens")
    private Integer maxTokens;
    /**
     *
     */
    @TableField(value = "active")
    private Boolean active;
    /**
     *
     */
    @TableField(value = "app_id")
    private String appId;
    /**
     *
     */
    @TableField(value = "api_secret")
    private String apiSecret;
}
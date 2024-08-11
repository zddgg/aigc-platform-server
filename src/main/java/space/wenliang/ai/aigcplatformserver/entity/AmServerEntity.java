package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName am_server
 */
@TableName(value = "am_server")
@Data
public class AmServerEntity implements Serializable {
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
    @TableField(value = "api_version")
    private String apiVersion;
}
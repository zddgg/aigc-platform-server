package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value = "tm_prompt_template")
@Data
public class TmPromptTemplateEntity implements Serializable {
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
    @TableField(value = "template_group")
    private String templateGroup;
    /**
     *
     */
    @TableField(value = "is_default")
    private Boolean isDefault;
    /**
     *
     */
    @TableField(value = "template_name")
    private String templateName;
    /**
     *
     */
    @TableField(value = "system_prompt")
    private String systemPrompt;
    /**
     *
     */
    @TableField(value = "user_prompt")
    private String userPrompt;
    /**
     *
     */
    @TableField(value = "is_preset")
    private Boolean isPreset;
}

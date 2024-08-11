package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName am_prompt_audio
 */
@TableName(value = "am_prompt_audio")
@Data
public class AmPromptAudioEntity implements Serializable {
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
    @TableField(value = "pa_id")
    private String paId;
    /**
     *
     */
    @TableField(value = "pa_group")
    private String paGroup;
    /**
     *
     */
    @TableField(value = "pa_group_sort")
    private Integer paGroupSort;
    /**
     *
     */
    @TableField(value = "pa_group_show")
    private Boolean paGroupShow;
    /**
     *
     */
    @TableField(value = "pa_role")
    private String paRole;
    /**
     *
     */
    @TableField(value = "pa_role_gender")
    private String paRoleGender;
    /**
     *
     */
    @TableField(value = "pa_role_age")
    private String paRoleAge;
    /**
     *
     */
    @TableField(value = "pa_role_lang")
    private String paRoleLang;
    /**
     *
     */
    @TableField(value = "pa_role_tags")
    private String paRoleTags;
    /**
     *
     */
    @TableField(value = "pa_role_avatar")
    private String paRoleAvatar;
    /**
     *
     */
    @TableField(value = "pa_mood")
    private String paMood;
    /**
     *
     */
    @TableField(value = "pa_mood_avatar")
    private String paMoodAvatar;
    /**
     *
     */
    @TableField(value = "pa_audio")
    private String paAudio;
    /**
     *
     */
    @TableField(value = "pa_audio_text")
    private String paAudioText;
    /**
     *
     */
    @TableField(value = "pa_audio_lang")
    private String paAudioLang;
    /**
     *
     */
    @TableField(value = "pa_audio_tags")
    private String paAudioTags;
}
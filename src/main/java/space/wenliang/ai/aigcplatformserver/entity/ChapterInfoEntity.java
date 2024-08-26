package space.wenliang.ai.aigcplatformserver.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import space.wenliang.ai.aigcplatformserver.bean.TextMarkupInfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @TableName chapter_info
 */
@TableName(value = "chapter_info")
@Data
public class ChapterInfoEntity extends AudioRoleInfo implements Serializable {
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
    @TableField(value = "project_id")
    private String projectId;
    /**
     *
     */
    @TableField(value = "chapter_id")
    private String chapterId;
    /**
     *
     */
    @TableField(value = "para_index")
    private Integer paraIndex;
    /**
     *
     */
    @TableField(value = "sent_index")
    private Integer sentIndex;
    /**
     *
     */
    @TableField(value = "text_id")
    private String textId;
    /**
     *
     */
    @TableField(value = "text")
    private String text;
    /**
     *
     */
    @TableField(value = "text_lang")
    private String textLang;
    /**
     *
     */
    @TableField(value = "text_sort")
    private Integer textSort;
    /**
     *
     */
    @TableField(value = "dialogue_flag")
    private Boolean dialogueFlag;
    /**
     *
     */
    @TableField(value = "audio_volume")
    private Double audioVolume;
    /**
     *
     */
    @TableField(value = "audio_speed")
    private Double audioSpeed;
    /**
     *
     */
    @TableField(value = "audio_interval")
    private Integer audioInterval;
    /**
     *
     */
    @TableField(value = "audio_length")
    private Long audioLength;
    /**
     *
     */
    @TableField(value = "audio_files")
    private String audioFiles;
    /**
     *
     */
    @TableField(value = "audio_task_state")
    private Integer audioTaskState;
    /**
     *
     */
    @TableField(value = "text_markup_info_json")
    private String textMarkupInfoJson;

    public String getIndex() {
        if (Objects.nonNull(paraIndex) && Objects.nonNull(sentIndex)) {
            return paraIndex + "-" + sentIndex;
        }
        return null;
    }

    public TextMarkupInfo getTextMarkupInfo() {
        if (Objects.nonNull(textMarkupInfoJson)) {
            return JSON.parseObject(textMarkupInfoJson, TextMarkupInfo.class);
        }
        return new TextMarkupInfo();
    }
}
package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class RefAudioEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String refAudioId;

    private String audioGroup;
    private Integer groupSortOrder;
    private Boolean groupShowFlag;
    private String audioName;
    private String gender;
    private String ageGroup;
    private String language;
    private String tags;
    private String avatar;

    private String moodName;
    private String moodAvatar;

    private String moodAudioName;
    private String moodAudioText;
    private String moodAudioLang;
    private String moodAudioTags;
}

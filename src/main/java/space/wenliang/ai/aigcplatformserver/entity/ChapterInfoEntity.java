package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.bean.AudioModelConfigExt;

import java.util.Objects;

@Getter
@Setter
@TableName
public class ChapterInfoEntity extends AudioModelConfigExt {

    public static final int init = 0;
    public static final int created = 1;
    public static final int modified = 2;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String projectId;

    private String chapterId;

    private Integer paragraphIndex;

    private Integer splitIndex;

    private Integer sentenceIndex;

    private String text;

    private String textLang;

    private Boolean dialogueFlag;

    private String role;

    private String audioModelType;
    private String audioModelId;
    private String audioConfigId;
    private String refAudioId;

    private Double audioVolume;

    private Double audioSpeed;

    private Integer nextAudioInterval;

    private Integer audioState;

    private Long audioLength;

    private Boolean audioExportFlag;

    private Integer sortOrder;

    private String phoneticInfo;

    private String audioInstruct;

    @TableField(exist = false)
    private String index;

    public String getIndex() {
        if (Objects.nonNull(paragraphIndex) && Objects.nonNull(splitIndex) && Objects.nonNull(sentenceIndex)) {
            return paragraphIndex + "-" + splitIndex + "-" + sentenceIndex;
        }
        return null;
    }

    public String getSecondIndex() {
        if (Objects.nonNull(paragraphIndex) && Objects.nonNull(splitIndex)) {
            return paragraphIndex + "-" + splitIndex;
        }
        return null;
    }
}

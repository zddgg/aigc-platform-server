package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class GptSovitsConfigEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String configId;
    private String configName;

    private Float temperature;
    private Float topP;
    private Integer topK;
    private Float repetitionPenalty;
    private Integer batchSize;
    private Boolean parallelInfer;
    private Boolean splitBucket;
    private Integer seed;
    private String textSplitMethod;
    private Float fragmentInterval;
    private Float speedFactor;

    private String modelId;
    private String moodAudioId;

    @TableField(exist = false)
    private String text;
}

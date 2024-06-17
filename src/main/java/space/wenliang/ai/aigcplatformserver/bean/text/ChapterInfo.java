package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import space.wenliang.ai.aigcplatformserver.bean.model.ModelConfig;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ChapterInfo extends Role {

    public static final int init = 0;
    public static final int created = 1;
    public static final int modified = 2;

    private String index;
    private Integer p;
    private Integer s;
    private String text;
    private Boolean linesFlag = false;

    private Integer volume = 100;
    private Double speed = 1.0;
    private Integer interval = 500;
    private Boolean export;

    private String textLang;
    private Boolean audioModifiedFlag = false;

    // 0-init, 1-created, 2-modified, 3-waiting, 4-processing
    private int audioStage;
    private String audioUrl;
    private String audioPath;
    private Long lengthInMs;

    private byte[] audioBytes;

    public ChapterInfo(Integer p, Integer s, String text) {
        this.p = p;
        this.s = s;
        this.text = text;
    }

    public ChapterInfo(Integer p, Integer s, String text, Boolean linesFlag) {
        this.p = p;
        this.s = s;
        this.text = text;
        this.linesFlag = linesFlag;
    }

    public void setRoleInfo(Role role) {
        if (Objects.isNull(role)) {
            return;
        }
        if (StringUtils.isNotBlank(role.getRole())) {
            this.setRole(role.getRole());
        }
        if (StringUtils.isNotBlank(role.getGender())) {
            this.setGender(role.getGender());
        }
        if (StringUtils.isNotBlank(role.getAgeGroup())) {
            this.setAgeGroup(role.getAgeGroup());
        }
    }

    public void setModelConfig(ModelConfig modelConfig) {
        if (Objects.isNull(modelConfig)) {
            return;
        }
        super.setModelConfig(modelConfig);

        this.setAudioStage(modified);
    }

    public String getIndex() {
        if (Objects.nonNull(this.p) && Objects.nonNull(this.s)) {
            return this.p + "-" + this.s;
        }
        return index;
    }

    public void setModified() {
        this.setAudioStage(modified);
    }
}

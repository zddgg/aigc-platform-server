package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Objects;

@Data
public class AudioModelInfo extends AudioModelInfoKey {

    /**
     *
     */
    @TableField(value = "am_pa_group")
    public String amPaGroup;

    /**
     *
     */
    @TableField(value = "am_pa_role")
    public String amPaRole;

    /**
     *
     */
    @TableField(value = "am_pa_mood")
    public String amPaMood;

    /**
     *
     */
    @TableField(value = "am_pa_audio")
    public String amPaAudio;

    /**
     *
     */
    @TableField(value = "am_pa_audio_text")
    public String amPaAudioText;

    /**
     *
     */
    @TableField(value = "am_pa_audio_lang")
    public String amPaAudioLang;

    /**
     *
     */
    @TableField(value = "am_mf_group")
    public String amMfGroup;

    /**
     *
     */
    @TableField(value = "am_mf_role")
    public String amMfRole;

    /**
     *
     */
    @TableField(value = "am_mf_json")
    public String amMfJson;

    /**
     *
     */
    @TableField(value = "am_mc_name")
    public String amMcName;

    /**
     *
     */
    @TableField(value = "am_mc_params_json")
    public String amMcParamsJson;


    public void setPromptAudio(AmPromptAudioEntity promptAudio) {
        if (Objects.nonNull(promptAudio)) {
            this.amPaId = promptAudio.getPaId();
            this.amPaGroup = promptAudio.getPaGroup();
            this.amPaRole = promptAudio.getPaRole();
            this.amPaMood = promptAudio.getPaMood();
            this.amPaAudio = promptAudio.getPaAudio();
            this.amPaAudioText = promptAudio.getPaAudioText();
            this.amPaAudioLang = promptAudio.getPaAudioLang();
        } else {
            this.amPaId = "-1";
        }
    }

    public void setModelFile(AmModelFileEntity modelFile) {
        if (Objects.nonNull(modelFile)) {
            this.amMfId = modelFile.getMfId();
            this.amMfGroup = modelFile.getMfGroup();
            this.amMfRole = modelFile.getMfRole();
            this.amMfJson = modelFile.getMfJson();
        } else {
            this.amMfId = "-1";
        }
    }

    public void setModelConfig(AmModelConfigEntity modelConfig) {
        if (Objects.nonNull(modelConfig)) {
            this.amMcId = modelConfig.getMcId();
            this.amMcName = modelConfig.getMcName();
            this.amMcParamsJson = modelConfig.getMcParamsJson();
        } else {
            this.amMcId = "-1";
        }
    }

    public void setAudioModelInfo(AudioModelInfo audioModelInfo) {
        if (Objects.isNull(audioModelInfo)) {
            return;
        }

        this.setAmType(audioModelInfo.getAmType());

        this.setAmPaId(audioModelInfo.getAmPaId());
        this.setAmPaGroup(audioModelInfo.getAmPaGroup());
        this.setAmPaRole(audioModelInfo.getAmPaRole());
        this.setAmPaMood(audioModelInfo.getAmPaMood());
        this.setAmPaAudio(audioModelInfo.getAmPaAudio());
        this.setAmPaAudioText(audioModelInfo.getAmPaAudioText());
        this.setAmPaAudioLang(audioModelInfo.getAmPaAudioLang());

        this.setAmMfId(audioModelInfo.getAmMfId());
        this.setAmMfGroup(audioModelInfo.getAmMfGroup());
        this.setAmMfRole(audioModelInfo.getAmMfRole());
        this.setAmMfJson(audioModelInfo.getAmMfJson());

        this.setAmMcId(audioModelInfo.getAmMcId());
        this.setAmMcName(audioModelInfo.getAmMcName());
        this.setAmMcParamsJson(audioModelInfo.getAmMcParamsJson());
    }
}

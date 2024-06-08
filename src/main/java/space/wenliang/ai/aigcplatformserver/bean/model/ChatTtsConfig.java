package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

@Data
public class ChatTtsConfig {

    private String configName;
    private String text;
    private Float temperature;
    private Float top_P;
    private Integer top_K;
    private Integer audio_seed_input;
    private Integer text_seed_input;
    private Boolean refine_text_flag;
    private String params_refine_text;

    private String url;
}

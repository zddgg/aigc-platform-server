package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;

@Getter
public enum CacheEnum {

    PINYIN(CacheConstants.PINYIN, Long.MAX_VALUE),

    AM_MODEL_FILE(CacheConstants.AM_MODEL_FILE, Long.MAX_VALUE),

    AM_MODEL_CONFIG(CacheConstants.AM_MODEL_CONFIG, Long.MAX_VALUE),

    AM_PROMPT_AUDIO(CacheConstants.AM_PROMPT_AUDIO, Long.MAX_VALUE),

    ;

    public final String name;

    public final Long expires;

    CacheEnum(String name, Long expires) {
        this.name = name;
        this.expires = expires;
    }
}

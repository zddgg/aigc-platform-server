package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;

@Getter
public enum CacheEnum {

    PINYIN("pinyin", Long.MAX_VALUE);

    public final String name;

    public final Long expires;

    CacheEnum(String name, Long expires) {
        this.name = name;
        this.expires = expires;
    }
}

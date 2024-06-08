package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;

@Getter
public enum ModelTypeEnum {

    gpt_sovits("gpt-sovits"),
    fish_speech("fish-speech"),
    edge_tts("edge-tts"),
    chat_tts("chat-tts"),
    ;

    private final String name;

    ModelTypeEnum(String name) {
        this.name = name;
    }
}

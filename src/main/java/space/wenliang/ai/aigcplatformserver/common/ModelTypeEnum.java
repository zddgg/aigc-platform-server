package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;

@Getter
public enum ModelTypeEnum {

    gpt_sovits("audio", "gpt-sovits"),
    fish_speech("audio", "fish-speech"),
    edge_tts("audio", "edge-tts"),
    chat_tts("audio", "chat-tts"),
    cosy_voice("audio", "cosy-voice"),
    ;

    private final String type;
    private final String name;

    ModelTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}

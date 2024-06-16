package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;

import java.util.List;

@Data
public class AudioModelParam {

    private String modelType;

    private List<String> model;

    private List<String> audio;

    private ChatTtsConfigEntity chatTtsConfig;
}

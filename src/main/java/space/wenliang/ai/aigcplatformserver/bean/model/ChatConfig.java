package space.wenliang.ai.aigcplatformserver.bean.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatConfig {

    private List<ChatModelParam> services = new ArrayList<>();
    private List<ChatModelParam> templates = new ArrayList<>();
}

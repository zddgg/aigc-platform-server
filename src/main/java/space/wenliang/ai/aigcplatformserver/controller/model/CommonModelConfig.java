package space.wenliang.ai.aigcplatformserver.controller.model;

import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.bean.model.GsvModel;
import space.wenliang.ai.aigcplatformserver.bean.model.ModelConfig;

import java.util.List;

@Getter
@Setter
public class CommonModelConfig extends ModelConfig {

    private List<GsvModel> models;
}

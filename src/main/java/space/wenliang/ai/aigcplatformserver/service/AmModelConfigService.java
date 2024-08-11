package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.AmModelConfigEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AmModelConfigService extends IService<AmModelConfigEntity> {

    AmModelConfigEntity getByMcId(String mcId);

    List<AmModelConfigEntity> getByModelType(String modelType, Integer showMode);

    void updateConfig(AmModelConfigEntity modelConfig);

    void syncEdgeTtsConfig() throws IOException;

    void updateEdgeTtsShowFlag(Map<String, Boolean> data);
}

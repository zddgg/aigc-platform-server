package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;

import java.util.List;

public interface BGptSovitsConfigService {

    List<GptSovitsConfigEntity> configs();

    List<GptSovitsConfigEntity> getByConfigName(String configName);

    void createOrUpdate(GptSovitsConfigEntity configEntity);

    void deleteConfig(GptSovitsConfigEntity config);
}

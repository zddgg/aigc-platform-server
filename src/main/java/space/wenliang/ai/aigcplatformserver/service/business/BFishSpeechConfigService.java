package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;

import java.util.List;

public interface BFishSpeechConfigService {

    List<FishSpeechConfigEntity> configs();

    List<FishSpeechConfigEntity> getByConfigName(String configName);

    void createOrUpdate(FishSpeechConfigEntity configEntity);

    void deleteConfig(FishSpeechConfigEntity config);
}

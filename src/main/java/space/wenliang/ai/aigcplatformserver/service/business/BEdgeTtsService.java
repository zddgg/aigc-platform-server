package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;

import java.util.List;

public interface BEdgeTtsService {
    List<EdgeTtsConfigEntity> configs();

    List<EdgeTtsSettingEntity> settings();

    void updateSetting(EdgeTtsSettingEntity edgeTtsSettingEntity);

    void syncConfigs();

    String playOrCreateAudio(String voice);
}

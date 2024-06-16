package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.AudioServerConfigEntity;

import java.util.List;

public interface BAudioServerConfigService {
    List<AudioServerConfigEntity> list();

    void updateConfig(AudioServerConfigEntity config);
}

package space.wenliang.ai.aigcplatformserver.service.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.AudioServerConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AAudioServerConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BAudioServerConfigService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BAudioServerConfigServiceImpl implements BAudioServerConfigService {

    private final AAudioServerConfigService aAudioServerConfigService;

    @Override
    public List<AudioServerConfigEntity> list() {
        return aAudioServerConfigService.list();
    }

    @Override
    public void updateConfig(AudioServerConfigEntity config) {
        aAudioServerConfigService.updateById(config);
    }
}

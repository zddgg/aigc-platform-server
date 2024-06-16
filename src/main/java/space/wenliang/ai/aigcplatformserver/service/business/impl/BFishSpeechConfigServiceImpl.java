package space.wenliang.ai.aigcplatformserver.service.business.impl;

import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AFishSpeechConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BFishSpeechConfigService;

import java.util.List;

@Service
public class BFishSpeechConfigServiceImpl implements BFishSpeechConfigService {

    private final AFishSpeechConfigService aFishSpeechConfigService;

    public BFishSpeechConfigServiceImpl(AFishSpeechConfigService aFishSpeechConfigService) {
        this.aFishSpeechConfigService = aFishSpeechConfigService;
    }

    @Override
    public List<FishSpeechConfigEntity> configs() {
        return aFishSpeechConfigService.list();
    }

    @Override
    public List<FishSpeechConfigEntity> getByConfigName(String configName) {
        return aFishSpeechConfigService.getByConfigName(configName);
    }

    @Override
    public void createConfig(FishSpeechConfigEntity configEntity) {
        aFishSpeechConfigService.save(configEntity);
    }

    @Override
    public void deleteConfig(FishSpeechConfigEntity config) {
        aFishSpeechConfigService.removeById(config);
    }
}

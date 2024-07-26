package space.wenliang.ai.aigcplatformserver.service.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AGptSovitsConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BGptSovitsConfigService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BGptSovitsConfigServiceImpl implements BGptSovitsConfigService {

    private final AGptSovitsConfigService aGptSovitsConfigService;

    @Override
    public List<GptSovitsConfigEntity> configs() {
        return aGptSovitsConfigService.list();
    }

    @Override
    public List<GptSovitsConfigEntity> getByConfigName(String configName) {
        return aGptSovitsConfigService.getByConfigName(configName);
    }

    @Override
    public void createOrUpdate(GptSovitsConfigEntity configEntity) {
        aGptSovitsConfigService.saveOrUpdate(configEntity);
    }

    @Override
    public void deleteConfig(GptSovitsConfigEntity config) {
        aGptSovitsConfigService.removeById(config);
    }
}

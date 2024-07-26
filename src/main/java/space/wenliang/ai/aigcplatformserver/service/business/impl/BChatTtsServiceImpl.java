package space.wenliang.ai.aigcplatformserver.service.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AChatTtsConfigService;
import space.wenliang.ai.aigcplatformserver.service.business.BChatTtsService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BChatTtsServiceImpl implements BChatTtsService {

    private final AChatTtsConfigService aChatTtsConfigService;

    @Override
    public List<ChatTtsConfigEntity> getByConfigName(String configName) {
        return aChatTtsConfigService.getByConfigName(configName);
    }

    @Override
    public List<ChatTtsConfigEntity> configs() {
        return aChatTtsConfigService.list();
    }

    @Override
    public void deleteConfig(ChatTtsConfigEntity config) {
        aChatTtsConfigService.removeById(config);
    }

    @Override
    public void createOrUpdate(ChatTtsConfigEntity chatTtsConfig) {
        aChatTtsConfigService.saveOrUpdate(chatTtsConfig);
    }
}

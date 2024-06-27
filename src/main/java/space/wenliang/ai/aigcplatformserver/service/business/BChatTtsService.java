package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;

import java.util.List;

public interface BChatTtsService {

    List<ChatTtsConfigEntity> getByConfigName(String configName);

    List<ChatTtsConfigEntity> configs();

    void deleteConfig(ChatTtsConfigEntity config);

    void createOrUpdate(ChatTtsConfigEntity chatTtsConfig);
}

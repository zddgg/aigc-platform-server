package space.wenliang.ai.aigcplatformserver.service.business.impl;

import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelTemplateEntity;
import space.wenliang.ai.aigcplatformserver.service.application.AChatModelConfigService;
import space.wenliang.ai.aigcplatformserver.service.application.AChatModelTemplateService;
import space.wenliang.ai.aigcplatformserver.service.business.BChatModelConfigService;

import java.util.List;

@Service
public class BChatModelConfigServiceImpl implements BChatModelConfigService {

    private final AChatModelConfigService aChatModelConfigService;
    private final AChatModelTemplateService aChatModelTemplateService;

    public BChatModelConfigServiceImpl(AChatModelConfigService aChatModelConfigService, AChatModelTemplateService aChatModelTemplateService) {
        this.aChatModelConfigService = aChatModelConfigService;
        this.aChatModelTemplateService = aChatModelTemplateService;
    }

    @Override
    public List<ChatModelConfigEntity> list() {
        return aChatModelConfigService.list();
    }

    @Override
    public List<ChatModelTemplateEntity> templateList() {
        return aChatModelTemplateService.list();
    }

    @Override
    public void updateModelChatConfig(ChatModelConfigEntity chatModelConfigEntity) {
        aChatModelConfigService.saveOrUpdate(chatModelConfigEntity);
    }

    @Override
    public void deleteModelChatConfig(ChatModelConfigEntity chatModelConfigEntity) {
        aChatModelConfigService.removeById(chatModelConfigEntity);
    }

    @Override
    public void activeModelChatConfig(ChatModelConfigEntity chatModelConfigEntity) {
        aChatModelConfigService.activeConfig(chatModelConfigEntity.getId());
    }
}

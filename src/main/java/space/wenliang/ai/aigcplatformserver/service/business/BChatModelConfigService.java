package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelTemplateEntity;

import java.util.List;

public interface BChatModelConfigService {

    List<ChatModelConfigEntity> list();

    List<ChatModelTemplateEntity> templateList();

    void updateModelChatConfig(ChatModelConfigEntity chatModelConfigEntity);

    void deleteModelChatConfig(ChatModelConfigEntity chatModelConfigEntity);

    void activeModelChatConfig(ChatModelConfigEntity chatModelConfigEntity);
}

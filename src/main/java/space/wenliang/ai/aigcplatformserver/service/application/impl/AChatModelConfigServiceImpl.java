package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChatModelConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AChatModelConfigService;

@Service
public class AChatModelConfigServiceImpl extends ServiceImpl<ChatModelConfigMapper, ChatModelConfigEntity>
        implements AChatModelConfigService {

    private final ChatModelConfigMapper chatModelConfigMapper;

    public AChatModelConfigServiceImpl(ChatModelConfigMapper chatModelConfigMapper) {
        this.chatModelConfigMapper = chatModelConfigMapper;
    }

    @Override
    public void activeConfig(Integer id) {
        chatModelConfigMapper.activeConfig(id);
    }
}

package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChatTtsConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AChatTtsConfigService;

import java.util.List;

@Service
public class AChatTtsConfigServiceImpl extends ServiceImpl<ChatTtsConfigMapper, ChatTtsConfigEntity>
        implements AChatTtsConfigService {
    @Override
    public List<ChatTtsConfigEntity> getByConfigName(String configName) {
        return this.list(new LambdaQueryWrapper<ChatTtsConfigEntity>()
                .eq(ChatTtsConfigEntity::getConfigName, configName));
    }
}

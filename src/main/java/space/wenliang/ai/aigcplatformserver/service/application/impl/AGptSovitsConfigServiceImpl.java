package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.GptSovitsConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AGptSovitsConfigService;

import java.util.List;

@Service
public class AGptSovitsConfigServiceImpl extends ServiceImpl<GptSovitsConfigMapper, GptSovitsConfigEntity>
        implements AGptSovitsConfigService {

    @Override
    public List<GptSovitsConfigEntity> getByConfigName(String configName) {
        return super.list(new LambdaQueryWrapper<GptSovitsConfigEntity>()
                .eq(GptSovitsConfigEntity::getConfigName, configName));
    }
}

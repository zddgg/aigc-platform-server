package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.FishSpeechConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AFishSpeechConfigService;

import java.util.List;

@Service
public class AFishSpeechConfigServiceImpl extends ServiceImpl<FishSpeechConfigMapper, FishSpeechConfigEntity>
        implements AFishSpeechConfigService {

    @Override
    public List<FishSpeechConfigEntity> getByConfigName(String configName) {
        return super.list(new LambdaQueryWrapper<FishSpeechConfigEntity>()
                .eq(FishSpeechConfigEntity::getConfigName, configName));
    }
}

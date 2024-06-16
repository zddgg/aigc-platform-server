package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.AudioServerConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.AudioServerConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AAudioServerConfigService;

@Service
public class AAudioServerConfigServiceImpl extends ServiceImpl<AudioServerConfigMapper, AudioServerConfigEntity>
        implements AAudioServerConfigService {
}

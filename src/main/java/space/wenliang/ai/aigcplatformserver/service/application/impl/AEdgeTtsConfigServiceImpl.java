package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.EdgeTtsConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AEdgeTtsConfigService;

@Service
public class AEdgeTtsConfigServiceImpl extends ServiceImpl<EdgeTtsConfigMapper, EdgeTtsConfigEntity>
        implements AEdgeTtsConfigService {
}

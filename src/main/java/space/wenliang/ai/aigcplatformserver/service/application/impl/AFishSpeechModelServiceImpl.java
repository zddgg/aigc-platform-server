package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechModelEntity;
import space.wenliang.ai.aigcplatformserver.mapper.FishSpeechModelMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AFishSpeechModelService;

@Service
public class AFishSpeechModelServiceImpl extends ServiceImpl<FishSpeechModelMapper, FishSpeechModelEntity>
        implements AFishSpeechModelService {
}

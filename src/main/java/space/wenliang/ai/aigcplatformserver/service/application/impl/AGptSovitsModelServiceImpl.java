package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsModelEntity;
import space.wenliang.ai.aigcplatformserver.mapper.GptSovitsModelMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AGptSovitsModelService;

@Service
public class AGptSovitsModelServiceImpl extends ServiceImpl<GptSovitsModelMapper, GptSovitsModelEntity>
        implements AGptSovitsModelService {
}

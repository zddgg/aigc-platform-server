package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelTemplateEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChatModelTemplateMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AChatModelTemplateService;

@Service
public class AChatModelTemplateServiceImpl extends ServiceImpl<ChatModelTemplateMapper, ChatModelTemplateEntity>
        implements AChatModelTemplateService {
}

package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TmPromptTemplateEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TmPromptTemplateMapper;
import space.wenliang.ai.aigcplatformserver.service.TmPromptTemplateService;

@Service
public class TmPromptTemplateServiceImpl extends ServiceImpl<TmPromptTemplateMapper, TmPromptTemplateEntity>
        implements TmPromptTemplateService {

}

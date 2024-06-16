package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

public interface AChatModelConfigService extends IService<ChatModelConfigEntity> {

    void activeConfig(Integer id);
}

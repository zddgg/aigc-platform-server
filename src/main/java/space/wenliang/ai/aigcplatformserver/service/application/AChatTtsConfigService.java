package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.ChatTtsConfigEntity;

import java.util.List;

public interface AChatTtsConfigService extends IService<ChatTtsConfigEntity> {
    List<ChatTtsConfigEntity> getByConfigName(String configName);
}

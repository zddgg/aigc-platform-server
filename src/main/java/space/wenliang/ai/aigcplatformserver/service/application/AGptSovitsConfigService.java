package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.GptSovitsConfigEntity;

import java.util.List;

public interface AGptSovitsConfigService extends IService<GptSovitsConfigEntity> {

    List<GptSovitsConfigEntity> getByConfigName(String configName);
}

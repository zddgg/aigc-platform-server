package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechConfigEntity;

import java.util.List;

public interface AFishSpeechConfigService extends IService<FishSpeechConfigEntity> {

    List<FishSpeechConfigEntity> getByConfigName(String configName);
}

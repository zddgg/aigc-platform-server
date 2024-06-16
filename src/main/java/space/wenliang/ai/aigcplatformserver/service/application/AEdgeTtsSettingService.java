package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;

import java.util.List;

public interface AEdgeTtsSettingService extends IService<EdgeTtsSettingEntity> {

    void deleteByEnNames(List<String> enNames);
}

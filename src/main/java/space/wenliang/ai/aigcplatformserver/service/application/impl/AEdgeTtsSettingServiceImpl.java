package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;
import space.wenliang.ai.aigcplatformserver.mapper.EdgeTtsSettingMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AEdgeTtsSettingService;

import java.util.List;

@Service
public class AEdgeTtsSettingServiceImpl extends ServiceImpl<EdgeTtsSettingMapper, EdgeTtsSettingEntity>
        implements AEdgeTtsSettingService {

    @Override
    public void deleteByEnNames(List<String> enNames) {
        if (CollectionUtils.isEmpty(enNames)) {
            return;
        }
        this.remove(new LambdaQueryWrapper<EdgeTtsSettingEntity>()
                .in(EdgeTtsSettingEntity::getEnName, enNames));
    }
}

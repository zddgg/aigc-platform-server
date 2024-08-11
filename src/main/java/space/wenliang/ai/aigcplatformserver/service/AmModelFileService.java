package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.AmModelFileEntity;

import java.util.List;

public interface AmModelFileService extends IService<AmModelFileEntity> {

    AmModelFileEntity getByMfId(String mfId);

    void refreshCache(String modelType);

    List<AmModelFileEntity> getByModelType(String modelType);
}

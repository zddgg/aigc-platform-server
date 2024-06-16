package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.GptSovitsModelEntity;

import java.util.List;

public interface BGptSovitsModelService {

    List<GptSovitsModelEntity> list();

    void refreshCache();

}

package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.FishSpeechModelEntity;

import java.util.List;

public interface BFishSpeechModelService {

    List<FishSpeechModelEntity> list();

    void refreshCache();
}

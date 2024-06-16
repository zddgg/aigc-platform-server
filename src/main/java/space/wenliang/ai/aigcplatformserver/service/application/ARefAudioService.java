package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.bean.RefAudioSort;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;

import java.util.List;

public interface ARefAudioService extends IService<RefAudioEntity> {

    List<RefAudioEntity> list();

    List<RefAudioEntity> allList();

    List<RefAudioEntity> getByGroupAndName(String group, String name);

    void updateRefAudioSort(RefAudioSort refAudioSort);
}

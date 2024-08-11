package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudio;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudioSort;
import space.wenliang.ai.aigcplatformserver.entity.AmPromptAudioEntity;

import java.util.List;

public interface AmPromptAudioService extends IService<AmPromptAudioEntity> {

    AmPromptAudioEntity getByPaId(String paId);

    void refreshCache();

    List<PromptAudioSort> queryPromptAudioSorts();

    List<PromptAudio> promptAudios();

    void updatePromptAudio(PromptAudio promptAudio);

    void updatePromptAudioSorts(List<PromptAudioSort> promptAudioSorts);
}

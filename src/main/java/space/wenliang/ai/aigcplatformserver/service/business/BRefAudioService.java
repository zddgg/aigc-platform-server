package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.bean.RefAudio;
import space.wenliang.ai.aigcplatformserver.bean.RefAudioSort;

import java.util.List;

public interface BRefAudioService {

    List<RefAudio> refAudioList();

    void refreshCache();

    void updateRefAudio(RefAudio refAudio);

    List<RefAudioSort> queryGroupSorts();

    void updateRefAudioSorts(List<RefAudioSort> refAudioSorts);
}

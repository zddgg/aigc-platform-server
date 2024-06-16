package space.wenliang.ai.aigcplatformserver.service.business;

import io.vavr.Tuple2;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

public interface BChapterInfoService {

    List<ChapterInfoEntity> chapterInfos(String projectId, String chapterId);

    void audioModelChange(ChapterInfoEntity chapterInfoEntity);

    void updateVolume(ChapterInfoEntity chapterInfoEntity);

    void updateSpeed(ChapterInfoEntity chapterInfoEntity);

    void updateInterval(ChapterInfoEntity chapterInfoEntity);

    void updateControls(ControlsUpdate controlsUpdate);

    void updateChapterText(ChapterInfoEntity chapterInfoEntity);

    List<String> addAudioCreateTask(ChapterInfoEntity chapterInfoEntity);

    Tuple2<Integer, List<String>> startCreateAudio(String projectId, String chapterId, String actionType);

    void stopCreateAudio();
}

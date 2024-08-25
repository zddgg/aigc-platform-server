package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.bean.PolyphonicParams;
import space.wenliang.ai.aigcplatformserver.bean.UpdateModelInfo;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

public interface BChapterInfoService {

    List<ChapterInfoEntity> chapterInfos(String projectId, String chapterId);

    void chapterInfoSort(List<ChapterInfoEntity> chapterInfoEntities);

    void audioModelChange(UpdateModelInfo updateModelInfo);

    void updateVolume(ChapterInfoEntity chapterInfoEntity);

    void updateSpeed(ChapterInfoEntity chapterInfoEntity);

    void updateInterval(ChapterInfoEntity chapterInfoEntity);

    void updateControls(ControlsUpdate controlsUpdate);

    void updateChapterText(ChapterInfoEntity chapterInfoEntity);

    void deleteChapterInfo(ChapterInfoEntity chapterInfoEntity);

    void addAudioCreateTask(ChapterInfoEntity chapterInfoEntity);

    void startCreateAudio(String projectId, String chapterId, String actionType);

    void stopCreateAudio();

    List<ChapterInfoEntity> chapterCondition(String projectId, String chapterId);

    void addPolyphonicInfo(PolyphonicParams polyphonicParams);

    void removePolyphonicInfo(PolyphonicParams polyphonicParams);

    ChapterInfoEntity addChapterInfo(ChapterInfoEntity chapterInfo);
}

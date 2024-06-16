package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

public interface AChapterInfoService extends IService<ChapterInfoEntity> {

    List<ChapterInfoEntity> list(String projectId, String chapterId);

    void deleteByProjectId(String projectId);

    void deleteByProjectIdAndChapterId(String projectId, String chapterId);

    void updateAudioStage(Integer id, int created);

    void audioModelReset(List<Integer> ids);
}

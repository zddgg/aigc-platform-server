package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.List;
import java.util.Map;

public interface ChapterInfoService extends IService<ChapterInfoEntity> {

    Map<String, Integer> chapterGroupCount();

    Map<String, Integer> chapterExportCount();

    void deleteByChapterId(String chapterId);

    List<ChapterInfoEntity> getByChapterId(String chapterId);

    List<ChapterInfoEntity> buildChapterInfos(TextChapterEntity textChapterEntity);

    void deleteByProjectId(String projectId);

    void audioModelReset(List<Integer> ids);
}

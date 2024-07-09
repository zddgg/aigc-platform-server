package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.Map;

public interface ATextChapterService extends IService<TextChapterEntity> {

    void deleteByProjectId(String projectId);

    void deleteByChapterId(String chapterId);

    TextChapterEntity getOne(String projectId, String chapterId);

    Map<String, Long> chapterCount();

    TextChapterEntity getTextChapterAndContent(String projectId, String chapterId);
}

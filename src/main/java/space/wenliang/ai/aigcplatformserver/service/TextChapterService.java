package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.Map;

public interface TextChapterService extends IService<TextChapterEntity> {

    Map<String, Integer> chapterCount();

    void deleteByChapterId(String chapterId);

    TextChapterEntity getTextChapterAndContent(String projectId, String chapterId);

    TextChapterEntity getByChapterId(String chapterId);

    void deleteByProjectId(String projectId);
}

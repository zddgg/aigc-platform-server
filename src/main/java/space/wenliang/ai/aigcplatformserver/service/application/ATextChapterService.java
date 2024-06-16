package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.List;
import java.util.Map;

public interface ATextChapterService extends IService<TextChapterEntity> {

    List<TextChapterEntity> list(String projectId);

    void delete(String projectId);

    TextChapterEntity getOne(String projectId, String chapterId);

    Map<String, Long> chapterCount();

    String getContent(String projectId, String chapterId);
}

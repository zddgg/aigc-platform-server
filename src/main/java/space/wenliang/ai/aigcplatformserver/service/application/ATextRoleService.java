package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.util.List;
import java.util.Map;

public interface ATextRoleService extends IService<TextRoleEntity> {

    List<TextRoleEntity> list(String projectId, String chapterId);

    void deleteByChapterId(String chapterId);

    void deleteByProjectId(String projectId);

    Map<String, Long> chapterGroupCount();
}

package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.util.List;
import java.util.Map;

public interface TextRoleService extends IService<TextRoleEntity> {

    Map<String, Integer> chapterGroupCount();

    void deleteByChapterId(String chapterId);

    List<TextRoleEntity> getByChapterId(String chapterId);

    void deleteByProjectId(String projectId);
}

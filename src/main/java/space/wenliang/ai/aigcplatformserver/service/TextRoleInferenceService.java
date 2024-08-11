package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleInferenceEntity;

import java.util.List;

public interface TextRoleInferenceService extends IService<TextRoleInferenceEntity> {

    void deleteByChapterId(String chapterId);

    void deleteByProjectId(String projectId);

    List<TextRoleInferenceEntity> getByChapterId(String chapterId);
}

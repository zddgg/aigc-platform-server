package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.RoleInferenceEntity;

import java.util.List;

public interface ARoleInferenceService extends IService<RoleInferenceEntity> {

    List<RoleInferenceEntity> list(String projectId, String chapterId);

    void delete(String projectId, String chapterId);

    void delete(String projectId);

}

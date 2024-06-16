package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;

import java.util.List;

public interface ATextProjectService extends IService<TextProjectEntity> {

    List<TextProjectEntity> list();

    TextProjectEntity getByProjectName(String getByProjectName);

    TextProjectEntity getOne(String projectId);

    TextProjectEntity getAndContentByProjectId(String projectId);

    void updateProjectName(String projectId, String projectName);

    void deleteByProjectId(String projectId);
}

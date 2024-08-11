package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;

public interface TextProjectService extends IService<TextProjectEntity> {

    TextProjectEntity getByProjectId(String projectId);

    TextProjectEntity getByProjectName(String projectName);

    void deleteByProjectId(String projectId);

    TextProjectEntity getAndContentByProjectId(String projectId);
}

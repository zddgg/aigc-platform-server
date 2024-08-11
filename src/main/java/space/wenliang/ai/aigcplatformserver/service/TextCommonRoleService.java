package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;

import java.util.List;

public interface TextCommonRoleService extends IService<TextCommonRoleEntity> {

    List<TextCommonRoleEntity> getByProjectId(String projectId);

    void deleteByProjectId(String projectId);
}

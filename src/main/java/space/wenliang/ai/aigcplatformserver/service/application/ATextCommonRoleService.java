package space.wenliang.ai.aigcplatformserver.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;

import java.util.List;

public interface ATextCommonRoleService extends IService<TextCommonRoleEntity> {

    List<TextCommonRoleEntity> list(String projectId);

    void deleteByProjectId(String projectId);
}

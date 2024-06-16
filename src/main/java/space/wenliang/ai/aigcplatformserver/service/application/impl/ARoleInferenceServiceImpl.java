package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.RoleInferenceEntity;
import space.wenliang.ai.aigcplatformserver.mapper.RoleInferenceMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ARoleInferenceService;

import java.util.List;

@Service
public class ARoleInferenceServiceImpl extends ServiceImpl<RoleInferenceMapper, RoleInferenceEntity>
        implements ARoleInferenceService {

    @Override
    public List<RoleInferenceEntity> list(String projectId, String chapterId) {
        return this.list(new LambdaQueryWrapper<RoleInferenceEntity>()
                .eq(RoleInferenceEntity::getProjectId, projectId)
                .eq(RoleInferenceEntity::getChapterId, chapterId));
    }

    @Override
    public void delete(String projectId, String chapterId) {
        this.remove(new LambdaQueryWrapper<RoleInferenceEntity>()
                .eq(RoleInferenceEntity::getProjectId, projectId)
                .eq(RoleInferenceEntity::getChapterId, chapterId));
    }

    @Override
    public void delete(String projectId) {
        this.remove(new LambdaQueryWrapper<RoleInferenceEntity>()
                .eq(RoleInferenceEntity::getProjectId, projectId));
    }
}

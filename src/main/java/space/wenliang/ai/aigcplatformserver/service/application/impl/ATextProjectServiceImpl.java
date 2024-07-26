package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextProjectMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ATextProjectService;

import java.util.List;

@Service
public class ATextProjectServiceImpl extends ServiceImpl<TextProjectMapper, TextProjectEntity>
        implements ATextProjectService {

    @Override
    public List<TextProjectEntity> list() {
        return this.list(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content")));
    }

    @Override
    public TextProjectEntity getByProjectName(String getByProjectName) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextProjectEntity::getProjectName, getByProjectName));
    }

    @Override
    public TextProjectEntity getOne(String projectId) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public TextProjectEntity getAndContentByProjectId(String projectId) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public void updateProjectName(String projectId, String projectName) {
        TextProjectEntity projectEntity = new TextProjectEntity();
        projectEntity.setProjectName(projectName);
        this.update(projectEntity, new LambdaUpdateWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, projectId));
    }
}

package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextProjectMapper;
import space.wenliang.ai.aigcplatformserver.service.TextProjectService;

@Service
public class TextProjectServiceImpl extends ServiceImpl<TextProjectMapper, TextProjectEntity>
        implements TextProjectService {

    @Override
    public TextProjectEntity getByProjectId(String projectId) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public TextProjectEntity getByProjectName(String projectName) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextProjectEntity::getProjectName, projectName));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public TextProjectEntity getAndContentByProjectId(String projectId) {
        return this.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, projectId));
    }
}





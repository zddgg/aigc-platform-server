package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextRoleMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ATextRoleService;

import java.util.List;

@Service
public class ATextRoleServiceImpl extends ServiceImpl<TextRoleMapper, TextRoleEntity>
        implements ATextRoleService {

    @Override
    public List<TextRoleEntity> list(String projectId, String chapterId) {
        return this.list(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public void delete(String projectId, String chapterId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public void delete(String projectId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId));
    }
}

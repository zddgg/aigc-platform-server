package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextCommonRoleMapper;
import space.wenliang.ai.aigcplatformserver.service.TextCommonRoleService;

import java.util.List;

@Service
public class TextCommonRoleServiceImpl extends ServiceImpl<TextCommonRoleMapper, TextCommonRoleEntity>
        implements TextCommonRoleService {

    @Override
    public List<TextCommonRoleEntity> getByProjectId(String projectId) {
        return super.list(new LambdaQueryWrapper<TextCommonRoleEntity>()
                .eq(TextCommonRoleEntity::getProjectId, projectId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextCommonRoleEntity>()
                .eq(TextCommonRoleEntity::getProjectId, projectId));
    }
}





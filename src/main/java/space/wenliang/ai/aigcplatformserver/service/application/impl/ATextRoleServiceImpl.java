package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextRoleMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ATextRoleService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ATextRoleServiceImpl extends ServiceImpl<TextRoleMapper, TextRoleEntity>
        implements ATextRoleService {

    private final TextRoleMapper textRoleMapper;

    @Override
    public List<TextRoleEntity> list(String projectId, String chapterId) {
        return this.list(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId));
    }

    @Override
    public Map<String, Long> chapterGroupCount() {
        return textRoleMapper.chapterGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public TextRoleEntity getByRole(String projectId, String chapterId, String role) {
        List<TextRoleEntity> roleEntities = this.list(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId)
                .eq(TextRoleEntity::getChapterId, chapterId)
                .eq(TextRoleEntity::getRole, role));
        if (CollectionUtils.isEmpty(roleEntities)) {
            return null;
        }
        return roleEntities.getFirst();
    }
}

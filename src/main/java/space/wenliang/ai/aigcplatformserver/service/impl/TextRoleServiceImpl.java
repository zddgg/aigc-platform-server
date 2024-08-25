package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextRoleMapper;
import space.wenliang.ai.aigcplatformserver.service.TextRoleService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextRoleServiceImpl extends ServiceImpl<TextRoleMapper, TextRoleEntity>
        implements TextRoleService {

    private final TextRoleMapper textRoleMapper;

    @Override
    public Map<String, Integer> chapterRoleGroupCount() {
        return textRoleMapper.chapterRoleGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public List<TextRoleEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextRoleEntity>()
                .eq(TextRoleEntity::getProjectId, projectId));
    }
}





package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleInferenceEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextRoleInferenceMapper;
import space.wenliang.ai.aigcplatformserver.service.TextRoleInferenceService;

import java.util.List;

@Service
public class TextRoleInferenceServiceImpl extends ServiceImpl<TextRoleInferenceMapper, TextRoleInferenceEntity>
        implements TextRoleInferenceService {

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<TextRoleInferenceEntity>()
                .eq(TextRoleInferenceEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextRoleInferenceEntity>()
                .eq(TextRoleInferenceEntity::getProjectId, projectId));
    }

    @Override
    public List<TextRoleInferenceEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<TextRoleInferenceEntity>()
                .eq(TextRoleInferenceEntity::getChapterId, chapterId));
    }
}





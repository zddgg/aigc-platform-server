package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextChapterMapper;
import space.wenliang.ai.aigcplatformserver.service.TextChapterService;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextChapterServiceImpl extends ServiceImpl<TextChapterMapper, TextChapterEntity>
        implements TextChapterService {

    private final TextChapterMapper textChapterMapper;

    @Override
    public Map<String, Integer> chapterCount() {
        return textChapterMapper.projectGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getChapterId, chapterId));
    }

    @Override
    public TextChapterEntity getTextChapterAndContent(String projectId, String chapterId) {
        return this.getOne(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getProjectId, projectId)
                .eq(TextChapterEntity::getChapterId, chapterId));
    }

    @Override
    public TextChapterEntity getByChapterId(String chapterId) {
        return this.getOne(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getProjectId, projectId));
    }
}





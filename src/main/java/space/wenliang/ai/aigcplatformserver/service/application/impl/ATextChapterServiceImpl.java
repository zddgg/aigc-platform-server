package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.mapper.TextChapterMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ATextChapterService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ATextChapterServiceImpl extends ServiceImpl<TextChapterMapper, TextChapterEntity>
        implements ATextChapterService {

    private final TextChapterMapper textChapterMapper;

    public ATextChapterServiceImpl(TextChapterMapper textChapterMapper) {
        this.textChapterMapper = textChapterMapper;
    }

    @Override
    public List<TextChapterEntity> list(String projectId) {
        return this.list(new LambdaQueryWrapper<TextChapterEntity>()
                .select(TextChapterEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextChapterEntity::getProjectId, projectId));
    }

    @Override
    public void delete(String projectId) {
        this.remove(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getProjectId, projectId));
    }

    @Override
    public TextChapterEntity getOne(String projectId, String chapterId) {
        return this.getOne(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getProjectId, projectId)
                .eq(TextChapterEntity::getChapterId, chapterId));
    }

    @Override
    public Map<String, Long> chapterCount() {
        return textChapterMapper.projectGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public String getContent(String projectId, String chapterId) {
        TextChapterEntity textChapter = this.getOne(new LambdaQueryWrapper<TextChapterEntity>()
                .eq(TextChapterEntity::getProjectId, projectId)
                .eq(TextChapterEntity::getChapterId, chapterId));
        if (Objects.nonNull(textChapter)) {
            return textChapter.getContent();
        }
        return null;
    }
}

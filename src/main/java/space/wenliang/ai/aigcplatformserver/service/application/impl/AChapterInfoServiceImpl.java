package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChapterInfoMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AChapterInfoService;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AChapterInfoServiceImpl extends ServiceImpl<ChapterInfoMapper, ChapterInfoEntity>
        implements AChapterInfoService {

    private final ChapterInfoMapper chapterInfoMapper;

    public AChapterInfoServiceImpl(ChapterInfoMapper chapterInfoMapper) {
        this.chapterInfoMapper = chapterInfoMapper;
    }

    @Override
    public List<ChapterInfoEntity> list(String projectId, String chapterId) {
        return this.list(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getProjectId, projectId)
                .eq(ChapterInfoEntity::getChapterId, chapterId));
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getProjectId, projectId));
    }

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getChapterId, chapterId));
    }

    @Override
    public void updateAudioStage(Integer id, int created) {
        this.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioState, created)
                .eq(ChapterInfoEntity::getId, id));
    }

    @Override
    public void audioModelReset(List<Integer> ids) {
        this.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAudioModelType, null)
                .set(ChapterInfoEntity::getAudioModelId, null)
                .set(ChapterInfoEntity::getAudioConfigId, null)
                .set(ChapterInfoEntity::getRefAudioId, null)
                .in(ChapterInfoEntity::getId, ids));
    }

    @Override
    public Map<String, Long> chapterGroupCount() {
        return chapterInfoMapper.chapterGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public Map<String, Boolean> chapterExportCount() {
        return chapterInfoMapper.chapterExportCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, v -> Objects.equals(v.getCount2(), Boolean.TRUE)));
    }

    @Override
    public List<ChapterInfoEntity> buildChapterInfos(TextChapterEntity textChapterEntity) {
        if (Objects.isNull(textChapterEntity) || StringUtils.isBlank(textChapterEntity.getContent())) {
            return new ArrayList<>();
        }

        List<String> dialoguePatterns = StringUtils.isBlank(textChapterEntity.getDialoguePattern())
                ? List.of()
                : List.of(textChapterEntity.getDialoguePattern());

        List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

        int paragraphIndex = 0;

        for (String line : textChapterEntity.getContent().split("\n")) {
            int splitIndex = 0;

            List<Tuple2<Boolean, List<String>>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

            for (Tuple2<Boolean, List<String>> chapterInfoTuple2 : chapterInfoTuple2s) {

                for (int i = 0; i < chapterInfoTuple2._2.size(); i++) {
                    ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                    chapterInfoEntity.setProjectId(textChapterEntity.getProjectId());
                    chapterInfoEntity.setChapterId(textChapterEntity.getChapterId());
                    chapterInfoEntity.setParagraphIndex(paragraphIndex);
                    chapterInfoEntity.setSplitIndex(splitIndex);
                    chapterInfoEntity.setSentenceIndex(i);
                    chapterInfoEntity.setText(chapterInfoTuple2._2.get(i));
                    chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._1);

                    chapterInfoEntity.setRole("旁白");

                    chapterInfoEntities.add(chapterInfoEntity);

                }

                splitIndex++;
            }

            paragraphIndex++;
        }

        return chapterInfoEntities;
    }
}

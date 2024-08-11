package space.wenliang.ai.aigcplatformserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChapterInfoMapper;
import space.wenliang.ai.aigcplatformserver.service.ChapterInfoService;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterInfoServiceImpl extends ServiceImpl<ChapterInfoMapper, ChapterInfoEntity>
        implements ChapterInfoService {

    private final ChapterInfoMapper chapterInfoMapper;

    @Override
    public Map<String, Integer> chapterGroupCount() {
        return chapterInfoMapper.chapterGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public Map<String, Integer> chapterExportCount() {
        return chapterInfoMapper.chapterExportCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    @Override
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getChapterId, chapterId));
    }

    @Override
    public List<ChapterInfoEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getChapterId, chapterId)
                .orderByAsc(ChapterInfoEntity::getTextSort)
                .orderByAsc(ChapterInfoEntity::getId));
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

        int paraIndex = 0;

        for (String line : textChapterEntity.getContent().split("\n")) {
            int sentIndex = 0;

            List<Tuple2<Boolean, String>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);

            for (Tuple2<Boolean, String> chapterInfoTuple2 : chapterInfoTuple2s) {

                ChapterInfoEntity chapterInfoEntity = new ChapterInfoEntity();
                chapterInfoEntity.setProjectId(textChapterEntity.getProjectId());
                chapterInfoEntity.setChapterId(textChapterEntity.getChapterId());
                chapterInfoEntity.setParaIndex(paraIndex);
                chapterInfoEntity.setSentIndex(sentIndex);
                chapterInfoEntity.setText(chapterInfoTuple2._2);
                chapterInfoEntity.setDialogueFlag(chapterInfoTuple2._1);

                chapterInfoEntity.setRole("旁白");

                chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.process);
                chapterInfoEntities.add(chapterInfoEntity);

                sentIndex++;
            }

            paraIndex++;
        }

        return chapterInfoEntities;
    }

    @Override
    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getProjectId, projectId));
    }

    @Override
    public void audioModelReset(List<Integer> ids) {
        this.update(new LambdaUpdateWrapper<ChapterInfoEntity>()
                .set(ChapterInfoEntity::getAmType, null)
                .set(ChapterInfoEntity::getAmPaId, null)
                .set(ChapterInfoEntity::getAmPaGroup, null)
                .set(ChapterInfoEntity::getAmPaRole, null)
                .set(ChapterInfoEntity::getAmPaMood, null)
                .set(ChapterInfoEntity::getAmPaAudio, null)
                .set(ChapterInfoEntity::getAmPaAudioText, null)
                .set(ChapterInfoEntity::getAmPaAudioLang, null)
                .set(ChapterInfoEntity::getAmMfId, null)
                .set(ChapterInfoEntity::getAmMfGroup, null)
                .set(ChapterInfoEntity::getAmMfRole, null)
                .set(ChapterInfoEntity::getAmMfJson, null)
                .set(ChapterInfoEntity::getAmMcId, null)
                .set(ChapterInfoEntity::getAmMcParamsJson, null)
                .in(ChapterInfoEntity::getId, ids));
    }
}





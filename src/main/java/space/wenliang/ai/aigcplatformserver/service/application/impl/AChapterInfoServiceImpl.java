package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ChapterInfoMapper;
import space.wenliang.ai.aigcplatformserver.service.application.AChapterInfoService;

import java.util.List;

@Service
public class AChapterInfoServiceImpl extends ServiceImpl<ChapterInfoMapper, ChapterInfoEntity>
        implements AChapterInfoService {

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
    public void deleteByProjectIdAndChapterId(String projectId, String chapterId) {
        this.remove(new LambdaQueryWrapper<ChapterInfoEntity>()
                .eq(ChapterInfoEntity::getProjectId, projectId)
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
}

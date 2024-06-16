package space.wenliang.ai.aigcplatformserver.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.RefAudioSort;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.mapper.RefAudioMapper;
import space.wenliang.ai.aigcplatformserver.service.application.ARefAudioService;

import java.util.List;

@Service
public class ARefAudioServiceImpl extends ServiceImpl<RefAudioMapper, RefAudioEntity>
        implements ARefAudioService {

    @Override
    public List<RefAudioEntity> list() {
        return this.list(new LambdaUpdateWrapper<RefAudioEntity>()
                .eq(RefAudioEntity::getGroupShowFlag, true));
    }

    @Override
    public List<RefAudioEntity> allList() {
        return super.list();
    }

    @Override
    public List<RefAudioEntity> getByGroupAndName(String group, String name) {
        return super.list(new LambdaQueryWrapper<RefAudioEntity>()
                .eq(RefAudioEntity::getAudioGroup, group)
                .eq(RefAudioEntity::getAudioName, name));
    }

    @Override
    public void updateRefAudioSort(RefAudioSort refAudioSort) {
        this.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .eq(RefAudioEntity::getAudioGroup, refAudioSort.getGroup())
                .set(RefAudioEntity::getGroupSortOrder, refAudioSort.getSortOrder())
                .set(RefAudioEntity::getGroupShowFlag, refAudioSort.getShowFlag()));
    }
}

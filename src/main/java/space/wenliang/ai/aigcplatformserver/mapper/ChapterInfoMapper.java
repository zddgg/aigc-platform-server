package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

public interface ChapterInfoMapper extends BaseMapper<ChapterInfoEntity> {

    @Select("select chapter_id as group1, count(*) as count1 " +
            "from chapter_info group by chapter_id")
    List<GroupCount> chapterGroupCount();

    @Select("select chapter_id as group1, max(audio_task_state) as count1 " +
            "from chapter_info group by chapter_id;")
    List<GroupCount> chapterExportCount();
}





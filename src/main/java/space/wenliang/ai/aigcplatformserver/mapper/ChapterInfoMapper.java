package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import space.wenliang.ai.aigcplatformserver.bean.ChapterSummary;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.util.List;

public interface ChapterInfoMapper extends BaseMapper<ChapterInfoEntity> {

    @Select("""
            select chapter_id                                         as chapter_id,
                   sum(char_length(text))                                  as word_count,
                   count(*)                                           as text_count,
                   sum(case when dialogue_flag = 1 then 1 else 0 end) as dialogue_count,
                   max(audio_task_state)                              as max_task_state
            from chapter_info
            group by chapter_id
            """)
    List<ChapterSummary> chapterSummary4MySQL();

    @Select("""
            select chapter_id                                         as chapter_id,
                   sum(length(text))                                  as word_count,
                   count(*)                                           as text_count,
                   sum(case when dialogue_flag = 1 then 1 else 0 end) as dialogue_count,
                   max(audio_task_state)                              as max_task_state
            from chapter_info
            group by chapter_id
            """)
    List<ChapterSummary> chapterSummary4SQLite();
}





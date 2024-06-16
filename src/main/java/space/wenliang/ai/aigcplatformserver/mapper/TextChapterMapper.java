package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.List;

@Repository
public interface TextChapterMapper extends BaseMapper<TextChapterEntity> {

    @Select("select project_id as group1, count(*) as count1 from text_chapter_entity group by project_id")
    List<GroupCount> projectGroupCount();
}

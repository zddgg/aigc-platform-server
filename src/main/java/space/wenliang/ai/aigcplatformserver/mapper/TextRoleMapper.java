package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.util.List;

@Repository
public interface TextRoleMapper extends BaseMapper<TextRoleEntity> {

    @Select("select chapter_id as group1, count(*) as count1 from text_role_entity group by chapter_id")
    List<GroupCount> chapterGroupCount();
}

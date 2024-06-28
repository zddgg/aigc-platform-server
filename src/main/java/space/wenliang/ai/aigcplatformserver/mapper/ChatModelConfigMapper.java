package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

@Repository
public interface ChatModelConfigMapper extends BaseMapper<ChatModelConfigEntity> {


    @Update("UPDATE chat_model_config_entity SET active = CASE WHEN id = #{id} THEN 1 ELSE 0 END")
    void activeConfig(Integer id);
}

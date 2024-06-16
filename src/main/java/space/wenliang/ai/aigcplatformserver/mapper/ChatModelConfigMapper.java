package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

@Repository
public interface ChatModelConfigMapper extends BaseMapper<ChatModelConfigEntity> {


    @Update("update chat_model_config_entity c set c.active = IF(c.id = #{id}, 1, 0)")
    void activeConfig(Integer id);
}

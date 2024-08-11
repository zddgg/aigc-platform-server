package space.wenliang.ai.aigcplatformserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;

@Getter
@Setter
public class TextProject extends TextProjectEntity {

    @TableField(exist = false)
    private Integer chapterCount;
}

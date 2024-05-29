package space.wenliang.ai.aigcplatformserver.bean.text;

import lombok.Data;

import java.util.List;

@Data
public class AiResult {

    private List<Role> roles;

    private List<LinesMapping> linesMappings;
}

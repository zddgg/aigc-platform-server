package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReq {

    private Long current = 1L;

    private Long pageSize = 10L;
}

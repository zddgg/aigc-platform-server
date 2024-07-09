package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.common.PageReq;

@Data
public class ProjectQuery extends PageReq {

    private String projectId;
}

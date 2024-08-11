package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TextMarkupInfo {

    private List<PolyphonicInfo> polyphonicInfos = new ArrayList<>();
}

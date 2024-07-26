package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class PhoneticAnno {
    private String type;
    private Integer index;
    private String pinyin;
}
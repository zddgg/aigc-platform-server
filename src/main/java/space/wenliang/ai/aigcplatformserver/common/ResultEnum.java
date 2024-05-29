package space.wenliang.ai.aigcplatformserver.common;

import lombok.Getter;

@Getter
public enum ResultEnum {

    SUCCESS("0000", "搞定~"),
    FAILURE("9999", "处理遇到了问题~"),
    PARAMETER_ERROR("0001", "参数缺失~"),
    ;


    private final String code;

    private final String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

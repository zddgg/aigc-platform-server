package space.wenliang.ai.aigcplatformserver.exception;

import lombok.Getter;
import space.wenliang.ai.aigcplatformserver.common.ResultEnum;

@Getter
public class BizException extends RuntimeException {

    public String code;

    public String msg;

    public Object data;

    public BizException(String msg) {
        super(msg);
        this.code = ResultEnum.FAILURE.getCode();
        this.msg = msg;
    }

    public BizException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(String code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

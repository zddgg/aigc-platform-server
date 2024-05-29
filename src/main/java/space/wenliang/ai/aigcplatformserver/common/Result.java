package space.wenliang.ai.aigcplatformserver.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    public String code;

    public String msg;

    public T data;

    public static Result<Object> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    public static Result<Object> failure() {
        return new Result<>(ResultEnum.FAILURE.getCode(), ResultEnum.FAILURE.getMsg(), null);
    }

    public static Result<Object> failure(String msg) {
        return new Result<>(ResultEnum.FAILURE.getCode(), msg, null);
    }

    public static Result<Object> failure(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static Result<Object> failure(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMsg(), null);
    }
}

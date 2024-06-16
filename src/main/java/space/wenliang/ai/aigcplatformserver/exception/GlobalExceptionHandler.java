package space.wenliang.ai.aigcplatformserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.common.ResultEnum;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result<Object> bizExceptionHandler(BizException e) {
        log.error("bizExceptionHandler, message: {}", e.getMessage(), e);
        return Result.failure(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return Result.failure(ResultEnum.PARAMETER_ERROR.getCode(), message);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<Object> exceptionHandler(Exception e) {
        log.error("exceptionHandler, message: {}", e.getMessage(), e);
        return Result.failure(e.getMessage());
    }
}

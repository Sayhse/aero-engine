package com.ly.aeroengine.handler;

import com.ly.aeroengine.exception.BizRuntimeException;
import com.ly.aeroengine.result.Result;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

import static com.ly.aeroengine.enums.BaseResultCodeEnum.ILLEGAL_ARGUMENT;
import static com.ly.aeroengine.enums.BaseResultCodeEnum.SYSTEM_ERROR;

/**
 * @Author ly
 * @Date 2024 03 12 22 18
 **/
@ControllerAdvice
public class WebValidatedExceptionHandler {
    /**
     * 处理请求参数格式错误 @RequestBody 上使用 @Valid 实体上使用@NotNull等，验证失败后抛出的异常是MethodArgumentNotValidException异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());
        return Result.ok().success(false).code(ILLEGAL_ARGUMENT.code()).message(message);
    }

    /**
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result<?> bindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());
        return Result.ok().success(false).code(ILLEGAL_ARGUMENT.code()).message(message);
    }

    /**
     * 参数格式异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result<?> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return Result.ok().success(false).code(ILLEGAL_ARGUMENT.code()).message("参数格式异常");
    }

    /**
     * 系统业务异常
     */
    @ExceptionHandler(BizRuntimeException.class)
    @ResponseBody
    public Result<?> bizExceptionHandler(BizRuntimeException e) {
        return Result.ok().success(false).code(e.getCode()).message(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> exceptionHandler(RuntimeException e) {
        return Result.ok().success(false).code(SYSTEM_ERROR.code()).message(SYSTEM_ERROR.message());
    }
}

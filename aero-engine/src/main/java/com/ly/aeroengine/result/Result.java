package com.ly.aeroengine.result;

import com.ly.aeroengine.enums.BaseResultCodeEnum;

/**
 * 通用返回结果
 *
 * @param <T>
 * @author fangu
 */
public class Result<T> extends ToString {

    /** 错误码，0表示成功，其他均表示失败 */
    private int code;

    /** 错误信息 */
    private String message;

    /** 业务信息（响应结果） */
    private T data;

    /** 业务是否执行成功 */
    private boolean success = true;

    public static <T> Result<T> ok(T d) {
        return new Result<>(BaseResultCodeEnum.SUCCESS, d);
    }

    public static <T> Result<T> ok() {
        return new Result<>(BaseResultCodeEnum.SUCCESS, null);
    }

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public Result(BaseResultCodeEnum resultEnum, T data) {
        this.code = resultEnum.code();
        this.message = resultEnum.message();
        this.data = data;
    }

    public static <T> Result<T> fail(BaseResultCodeEnum resultEnum) {
        return new Result<>(resultEnum.code(), resultEnum.message(), null, false);
    }

    public static <T> Result<T> fail(BaseResultCodeEnum resultEnum, T data) {
        return new Result<>(resultEnum.code(), resultEnum.message(), data, false);
    }

    public static <T> Result<T> fail(BaseResultCodeEnum resultEnum, String message) {
        return new Result<>(resultEnum.code(), message, null, false);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, false);
    }

    /**
     * 判断这次请求是否执行成功
     * @return 是否执行成功：true（成功）、false（失败）
     */
    public boolean ifSuccess() {
        return BaseResultCodeEnum.SUCCESS.code() == this.code;
    }

    public int getCode() {
        return code;
    }

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Result<T> success(boolean success) {
        this.success = success;
        return this;
    }
}

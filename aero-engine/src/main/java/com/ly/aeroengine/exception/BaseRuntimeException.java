package com.ly.aeroengine.exception;

import com.ly.aeroengine.enums.BaseResultCodeEnum;
import com.ly.aeroengine.enums.IResultCodeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 运行时异常基类
 * @author ly
 */
public class BaseRuntimeException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -8373310686430610622L;

    /** 异常码 */
    protected Integer code = BaseResultCodeEnum.SYSTEM_ERROR.code();

    /** 异常具体信息 */
    protected String message = BaseResultCodeEnum.SYSTEM_ERROR.message();

    /**
     * <br>
     * 原始异常信息：<br>
     * 异常可以通过 rpc 调用返回，此时得到的直接异常栈是关于 rpc 的被调用方的，因为 rpc 调用的异常是反序列化直接得到的<br>
     * 这样就会导致本地栈丢失，因为没有通过 new Exception(rpcException) 抛出异常，也不能这样做，因为会导致异常类信息的丢失<br>
     * 如果增加本地栈到 rpc 异常中，会导致随着调用链的增加，异常栈成倍放大（除非实现了异常压缩）<br>
     * 如果替换 rpcException 中的异常栈为本地栈，那么虽然当前服务定位方便了，需要找到最终源头更复杂了<br>
     */
    private StackTraceElement[] originalStackTraceElement;

    /**
     * 构造器。
     *
     * @param errorMessage 异常信息
     */
    public BaseRuntimeException(String errorMessage) {
        this.message = errorMessage;
    }

    /**
     * 构造器。
     *
     * @param errorCode 异常码
     * @param errorMessage 异常信息
     */
    public BaseRuntimeException(Integer errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }

    /**
     * 构造器。
     *
     * @param baseEnum 异常结果枚举
     */
    public BaseRuntimeException(IResultCodeEnum baseEnum) {
        this(baseEnum.code(), baseEnum.message());
    }

    /**
     * 构造器。
     *
     * @param baseEnum 异常结果枚举
     * @param errorMessage 异常信息
     */
    public BaseRuntimeException(IResultCodeEnum baseEnum, String errorMessage) {
        this(baseEnum.code(), errorMessage);
    }

    /**
     * 构造器。
     *
     * @param cause 原因
     */
    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造器。
     *
     * @param errorMessage 异常信息
     * @param cause 原因
     */
    public BaseRuntimeException(String errorMessage, Throwable cause) {
        super(cause);
        this.message = errorMessage;
    }

    /**
     * 构造器。
     *
     * @param errorCode 异常码
     * @param errorMessage 异常信息
     * @param cause 原因
     */
    public BaseRuntimeException(Integer errorCode, String errorMessage, Throwable cause) {
        super(cause);
        this.code = errorCode;
        this.message = errorMessage;
    }


    /**
     * 构造函数
     *
     * @param baseEnum 异常结果集枚举
     * @param cause 异常
     */
    public BaseRuntimeException(IResultCodeEnum baseEnum, Throwable cause) {
        this(baseEnum.code(), baseEnum.message(), cause);
    }

    /**
     * 构造函数
     *
     * @param baseEnum
     * @param errorMessage
     * @param cause
     */
    public BaseRuntimeException(
            IResultCodeEnum baseEnum, String errorMessage, Throwable cause) {
        this(baseEnum.code(), errorMessage, cause);
    }

    /** @see Throwable#toString() */
    @Override
    public String toString() {
        return getClass().getName() +
                "[" +
                "code=" +
                code +
                ",digestMessage=" +
                ",message=" +
                getMessage() +
                "]";
    }

    /**
     * 异常信息参数格式化 <br>
     * <a style='color:red;font-weight:800;'>此方法不建议使用，若发现此方法请当即删除。替代功能参照{@link
     * java.text.DateFormat}</a>
     *
     * @param args message中有需要传入的变量"s%,d%"之类的 eg：message:"this is a example! %s",args:"ok" 消息
     */
    @Deprecated
    public BaseRuntimeException formatter(Object... args) {
        if (!StringUtils.isEmpty(message)) {
            for (int i = 0; i < args.length; i++) {
                message = String.format(this.message, args);
            }
        }
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public BaseRuntimeException code(Integer code) {
        this.code = code;
        return this;
    }

    public BaseRuntimeException message(String message) {
        this.message = message;
        return this;
    }

}

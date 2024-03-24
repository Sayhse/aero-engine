package com.ly.aeroengine.exception;


import com.ly.aeroengine.enums.SysUserResultCodeEnum;

/**
 * @author ly
 * @since  2024-03-10
 */
public class SysUserServiceBizException extends BizRuntimeException {
    public SysUserServiceBizException(String errorMessage) {
        super(errorMessage);
    }

    public SysUserServiceBizException(Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public SysUserServiceBizException(SysUserResultCodeEnum codeEnum) {
        super(codeEnum.code(), codeEnum.message());
    }
}

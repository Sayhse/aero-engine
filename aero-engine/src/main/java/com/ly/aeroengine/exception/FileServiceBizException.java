package com.ly.aeroengine.exception;

import com.ly.aeroengine.enums.FileResultCodeEnum;
import com.ly.aeroengine.enums.SysUserResultCodeEnum;

/**
 * @Author ly
 * @Date 2024 03 13 16 50
 **/
public class FileServiceBizException extends BizRuntimeException{
    public FileServiceBizException(String errorMessage) {
        super(errorMessage);
    }

    public FileServiceBizException(Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public FileServiceBizException(FileResultCodeEnum codeEnum) {
        super(codeEnum.code(), codeEnum.message());
    }
}

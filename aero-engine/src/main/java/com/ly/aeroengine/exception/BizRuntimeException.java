package com.ly.aeroengine.exception;


import com.ly.aeroengine.enums.BaseResultCodeEnum;

/**
 * @author ly
 */
public class BizRuntimeException extends BaseRuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -3121133043898262409L;

    /**
     * @param errorMessage 错误信息
     */
    public BizRuntimeException(String errorMessage) {
        super(BaseResultCodeEnum.SYSTEM_ERROR, errorMessage);
    }

    /**
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    public BizRuntimeException(Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
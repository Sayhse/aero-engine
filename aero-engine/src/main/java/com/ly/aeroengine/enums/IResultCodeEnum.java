package com.ly.aeroengine.enums;

/**
 * 系统异常枚举基类
 * @author ly
 */
public interface IResultCodeEnum {

    /** 获取枚举名(建议与enum name、code保持一致) */
    String name();

    /** 获取枚举code */
    Integer code();

    /** 获取枚举message */
    String message();

}

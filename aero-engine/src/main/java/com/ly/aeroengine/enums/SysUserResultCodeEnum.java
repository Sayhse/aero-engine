package com.ly.aeroengine.enums;

import lombok.Getter;

/**
 * @Author ly
 * @Date 2024 03 12 17 21
 **/
public enum SysUserResultCodeEnum implements IResultCodeEnum{
    VERIFY_CODE_EXPIRE("VERIFY_CODE_EXPIRE","验证码已过期",3000000),
    VERIFY_CODE_ERROR("VERIFY_CODE_ERROR","验证码错误",3000001),
    USERNAME_PASSWORD_NOT_BLANK("USERNAME_PASSWORD_NOT_BLANK","用户账号或密码为空",3000002),
    ACCOUNT_NOT_EXIST("ACCOUNT_NOT_EXIST","用户不存在！",3000003),
    ACCOUNT_DELETED("ACCOUNT_DELETED","您的账号已注销！",3000004),
    PASSWORD_ERROR("PASSWORD_ERROR","密码错误！",3000005),
    ACCOUNT_EXIST("ACCOUNT_EXIST","您的账户已存在！",3000006),
    USERNAME_OVER_LENGTH("USERNAME_OVER_LENGTH","用户名超出长度限制！",3000007),
    REGISTER_ERROR("REGISTER_ERROR","注册失败！",3000008);



    /** 枚举标识name */
    @Getter
    private final String name;

    /** 枚举详情 */
    private final String message;

    /** 枚举编号code */
    private final Integer code;

    /**
     * 构造方法
     * @param name
     * @param message
     * @param code
     */
    SysUserResultCodeEnum(String name, String message, Integer code) {
        this.name = name;
        this.message = message;
        this.code = code;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}

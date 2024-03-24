package com.ly.aeroengine.entity.request;

import lombok.Data;

/**
 * @Author ly
 * @Date 2024 03 12 14 59
 **/
@Data
public class LoginUserRequest {
    /**
     * 账户名称/手机号码
     */
    private String username;

    /**
     * 用户密码/验证码
     */
    private String password;
    /**
     * 图形验证码 / 手机验证码
     */
    private String verifyCode;

    /**
     * 验证码 redis key
     */
    private String verifyCodeKey;
}

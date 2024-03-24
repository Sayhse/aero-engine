package com.ly.aeroengine.entity.request;

import lombok.Data;

/**
 * @Author ly
 * @Date 2024 03 12 22 44
 **/
@Data
public class RegisterUserRequest {
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户姓名
     */
    private String name;
    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户密码
     */
    private String password;
}

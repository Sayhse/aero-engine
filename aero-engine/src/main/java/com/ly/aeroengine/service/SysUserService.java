package com.ly.aeroengine.service;

import com.ly.aeroengine.entity.bo.LoginUserBo;
import com.ly.aeroengine.entity.bo.VerifyCodeBo;
import com.ly.aeroengine.entity.request.RegisterUserRequest;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author ly
 * @since 2024-03-12
 */
public interface SysUserService{

    Boolean checkVerifyCode(String verifyCodeKey, String verifyCode);

    LoginUserBo login(String username, String password);

    VerifyCodeBo generateVerifyCode();

    boolean register(RegisterUserRequest request);
}

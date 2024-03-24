package com.ly.aeroengine.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ly.aeroengine.constant.UserStatus;
import com.ly.aeroengine.entity.SysUser;
import com.ly.aeroengine.entity.bo.LoginUserBo;
import com.ly.aeroengine.entity.bo.VerifyCodeBo;
import com.ly.aeroengine.entity.request.RegisterUserRequest;
import com.ly.aeroengine.exception.SysUserServiceBizException;
import com.ly.aeroengine.mapper.SysUserMapper;
import com.ly.aeroengine.service.SysUserService;
import com.ly.aeroengine.service.login.PasswordService;
import com.ly.aeroengine.util.PasswordEncoder;
import com.ly.aeroengine.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.ly.aeroengine.constant.CacheKeyConstant.AUTH_VERIFY_CODE_KEY;
import static com.ly.aeroengine.enums.SysUserResultCodeEnum.*;
import static com.ly.aeroengine.util.SecurityUtil.TOKEN_EXP_TIME_HOUR;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author ly
 * @since 2024-03-12
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Boolean checkVerifyCode(String verifyCodeKey, String verifyCode) {
        log.info("校验验证码 verifyCodeKey:{}, verifyCode:{}", verifyCodeKey, verifyCode);
        String cacheKey = AUTH_VERIFY_CODE_KEY + verifyCodeKey;
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if(StrUtil.isBlank(cacheCode)) {
            throw new SysUserServiceBizException(VERIFY_CODE_EXPIRE);
        }
        if(!StrUtil.equals(verifyCode, cacheCode)) {
            throw new SysUserServiceBizException(VERIFY_CODE_ERROR);
        }
        return redisTemplate.delete(cacheKey);
    }

    @Override
    public LoginUserBo login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new SysUserServiceBizException(USERNAME_PASSWORD_NOT_BLANK);
        }
        SysUser user = sysUserMapper.queryByUserName(username);
        if (user == null || user.getUserId() == null) {
            throw new SysUserServiceBizException(ACCOUNT_NOT_EXIST);
        }
        // 账号已删除
        if (UserStatus.DELETED.equals(user.getDeleted())) {
            throw new SysUserServiceBizException(ACCOUNT_DELETED);
        }
        // 密码校验
        passwordService.validate(user, password);
        String token = SecurityUtil.createToken(
                user.getUserId(), user.getUserName(), user.getPhone());
        return LoginUserBo.builder()
                .userId(user.getUserId())
                .userName(user.getPassword())
                .phone(user.getPhone())
                .token(token)
                .expireTime(DateTime.now().offsetNew(DateField.HOUR, TOKEN_EXP_TIME_HOUR).getTime())
                .build();
    }

    @Override
    public VerifyCodeBo generateVerifyCode() {
        String uuid = IdUtil.simpleUUID();
        // 定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(120, 38, 4, 0);
        // 存入redis并设置过期时间为3分钟
        redisTemplate.opsForValue().set(
                AUTH_VERIFY_CODE_KEY + uuid, shearCaptcha.getCode(), 3, TimeUnit.MINUTES);
        String base64String = "";
        try {
            base64String = "data:image/png;base64," + shearCaptcha.getImageBase64();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new VerifyCodeBo(uuid, base64String);
    }

    @Override
    @Transactional
    public boolean register(RegisterUserRequest request) {
        SysUser user = sysUserMapper.queryByPhone(request.getPhone());
        if (user != null){
            throw new SysUserServiceBizException(ACCOUNT_EXIST);
        }
        if (request.getUserName().length() > 15)
            throw new SysUserServiceBizException(USERNAME_OVER_LENGTH);
        SysUser sysUser = new SysUser();
        sysUser.setUserName(request.getUserName());
        sysUser.setName(request.getName());
        String encodePw = passwordEncoder.aesPwdEncode(request.getPassword());
        sysUser.setPassword(encodePw);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setPhone(request.getPhone());
        int add = sysUserMapper.add(sysUser);
        if (add == 0){
            throw new SysUserServiceBizException(REGISTER_ERROR);
        }
        return true;
    }
}

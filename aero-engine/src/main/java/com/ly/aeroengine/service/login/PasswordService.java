package com.ly.aeroengine.service.login;

import cn.hutool.core.util.StrUtil;
import com.ly.aeroengine.constant.CacheKeyConstant;
import com.ly.aeroengine.entity.SysUser;
import com.ly.aeroengine.exception.SysUserServiceBizException;
import com.ly.aeroengine.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.ly.aeroengine.enums.SysUserResultCodeEnum.PASSWORD_ERROR;

/**
 * @Author ly
 * @Date 2024 03 12 16 00
 **/
@Component
public class PasswordService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheKeyConstant.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SysUser user, String password) {
        String username = user.getUserName();
        String cacheKey = getCacheKey(username);
        String value = redisTemplate.opsForValue().get(cacheKey);
        int retryCount = 0;
        if (StrUtil.isNotBlank(value)) {
            retryCount = Integer.parseInt(value);
        }
        if (retryCount >= CacheKeyConstant.PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format(
                    "密码输入错误%s次，帐户锁定%s分钟",
                    CacheKeyConstant.PASSWORD_MAX_RETRY_COUNT, CacheKeyConstant.PASSWORD_LOCK_TIME);
            throw new SysUserServiceBizException(PASSWORD_ERROR.code(), errMsg);
        }

        if (!matches(user, password)) {
            //密码错了
            retryCount = retryCount + 1;
            redisTemplate.opsForValue().set(
                    cacheKey, String.valueOf(retryCount),
                    CacheKeyConstant.PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
            throw new SysUserServiceBizException(PASSWORD_ERROR);
        } else {
            clearLoginRecordCache(username);
        }
    }

    /**
     * 返回true表示密码没错
     * @param user
     * @param rawPassword
     * @return
     */
    public boolean matches(SysUser user, String rawPassword) {
        return StrUtil.equals(user.getPassword(), passwordEncoder.aesPwdEncode(rawPassword));
    }

    public void clearLoginRecordCache(String loginName) {
        String cacheKey = getCacheKey(loginName);
        if (StrUtil.isNotBlank(redisTemplate.opsForValue().get(cacheKey))) {
            redisTemplate.delete(cacheKey);
        }
    }
}

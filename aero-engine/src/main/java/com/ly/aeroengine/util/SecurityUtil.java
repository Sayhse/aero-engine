package com.ly.aeroengine.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.ly.aeroengine.entity.SysUser;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/**
 * Token工具
 *
 * @author 徐晨晨
 * @since  2024-02-11
 */
@Slf4j
public class SecurityUtil {

    /**
     * 盐值很重要，不能泄漏，且每个项目都应该不一样，可以放到配置文件中
     */
    private static final String SALT_VALUE = "checkup-appointment-auth-salt";
    public static final int TOKEN_EXP_TIME_HOUR = 24;
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String PHONE = "phone";
    /**
     * 创建token
     * @param userId   用户id
     * @param username 用户name
     * @param phone    用户phone
     * @return token
     */
    public static String createToken(Long userId, String username, String phone) {
        Map<String, Object> payload = new HashMap<>();
        DateTime now = DateTime.now();
        // 签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        DateTime expTime = now.offsetNew(DateField.HOUR, TOKEN_EXP_TIME_HOUR);
        // 过期时间
        payload.put(JWTPayload.EXPIRES_AT, expTime);
        // 生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        // 自定义内容
        payload.put(USER_ID, userId);
        payload.put(USERNAME, username);
        payload.put(PHONE, phone);
        String token = JWTUtil.createToken(payload, SALT_VALUE.getBytes());
        log.info("生成JWT token：{}", token);
        return token;
    }

    /**
     * 校验token
     * @param token
     * @return
     */
    public static boolean validate(String token) {
        JWT jwt = JWTUtil.parseToken(token).setKey(SALT_VALUE.getBytes());
        // validate包含了verify
        boolean validate = jwt.validate(0);
        log.info("JWT token校验结果：{}", validate);
        return validate;
    }

    public static SysUser analysisToken(String token) {
        JWT jwt = JWTUtil.parseToken(token).setKey(SALT_VALUE.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        log.info("根据token获取原始内容：{}", payloads);
        String jsonString = payloads.toJSONString(0);
        return JSONUtil.toBean(jsonString, SysUser.class);
    }

}

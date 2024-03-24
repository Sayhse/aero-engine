package com.ly.aeroengine.constant;

/**
 * @Author ly
 * @Date 2024 03 12 15 13
 **/
public class CacheKeyConstant {
    public static final String AUTH_VERIFY_CODE_KEY = "user:login:verify_code";
    public static final String PWD_ERR_CNT_KEY = "user:login:password_error_num";
    public static final int PASSWORD_MAX_RETRY_COUNT = 10;
    public static final long PASSWORD_LOCK_TIME = 1;
}

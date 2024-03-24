package com.ly.aeroengine.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ly
 * @Date 2024 03 12 18 41
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeBo {
    /** 缓存key */
    private String verifyCodeKey;
    /** 图形验证码 */
    private String verifyCode;
}

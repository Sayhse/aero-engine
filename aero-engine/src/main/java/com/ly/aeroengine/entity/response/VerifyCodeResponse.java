package com.ly.aeroengine.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ly
 * @Date 2024 03 12 18 47
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeResponse {
    private String verifyCodeKey;
    private String verifyCode;
}

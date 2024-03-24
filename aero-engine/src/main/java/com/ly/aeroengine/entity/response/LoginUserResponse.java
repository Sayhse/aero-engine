package com.ly.aeroengine.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ly
 * @Date 2024 03 12 15 02
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {
    private String token;
    private Long expireTime;
}

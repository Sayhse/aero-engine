package com.ly.aeroengine.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.stereotype.Component;

/**
 * @Author ly
 * @Date 2024 03 12 16 13
 **/
@Component
public class PasswordEncoder {

    // 密钥
    private final byte[] KEY = "aes-password-key".getBytes();
    // 构建
    public SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, KEY);

    public String aesPwdEncode(String pwd) {
        // 加密
        return aes.encryptHex(pwd);
    }

    public String aesPwdDecode(String pwd) {
        // 解密为字符串
        return aes.decryptStr(pwd, CharsetUtil.CHARSET_UTF_8);
    }

}

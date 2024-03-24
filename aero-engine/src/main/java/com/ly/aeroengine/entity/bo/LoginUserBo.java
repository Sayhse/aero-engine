package com.ly.aeroengine.entity.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author ly
 * @Date 2024 03 12 16 44
 **/
@Data
@Builder
public class LoginUserBo {
    private static final long serialVersionUID = 1L;
    /** 用户认证/登录 token */
    private String token;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String deleted;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /** 过期时间 */
    private Long expireTime;

}

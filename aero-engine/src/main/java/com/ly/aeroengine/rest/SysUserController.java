package com.ly.aeroengine.rest;


import com.ly.aeroengine.entity.bo.LoginUserBo;
import com.ly.aeroengine.entity.bo.VerifyCodeBo;
import com.ly.aeroengine.entity.request.LoginUserRequest;
import com.ly.aeroengine.entity.request.RegisterUserRequest;
import com.ly.aeroengine.entity.response.LoginUserResponse;
import com.ly.aeroengine.entity.response.RegisterUserResponse;
import com.ly.aeroengine.entity.response.VerifyCodeResponse;
import com.ly.aeroengine.result.Result;
import com.ly.aeroengine.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author ly
 * @since 2024-03-12
 */
@RestController
@RequestMapping("/sys-user")
public class SysUserController {


    @Autowired
    private SysUserService sysUserServiceService;

    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("/auth/login")
    public Result<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        // 校验验证码
        sysUserServiceService.checkVerifyCode(request.getVerifyCodeKey(), request.getVerifyCode());
        LoginUserBo userBo = sysUserServiceService.login(request.getUsername(), request.getPassword());
        LoginUserResponse response =
                new LoginUserResponse(userBo.getToken(), userBo.getExpireTime());
        return Result.ok(response);
    }

    /**
     * 图片的base64编码字符串
     * redis存储
     * 接口需添加白名单放行
     *
     * @return String
     */
    @GetMapping("/auth/verifyCode")
    public Result<VerifyCodeResponse> verifyCode() {
        VerifyCodeBo verifyCodeBo = sysUserServiceService.generateVerifyCode();
        VerifyCodeResponse response = new VerifyCodeResponse(
                verifyCodeBo.getVerifyCodeKey(), verifyCodeBo.getVerifyCode());
        return Result.ok(response);
    }

    @PostMapping("/auth/register")
    public Result<RegisterUserResponse> register(@RequestBody RegisterUserRequest request){
        sysUserServiceService.register(request);
        RegisterUserResponse response = new RegisterUserResponse("注册成功！");
        return Result.ok(response);
    }
}

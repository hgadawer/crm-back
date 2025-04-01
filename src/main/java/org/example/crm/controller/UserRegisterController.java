package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.UserInfoDTO;
import org.example.crm.DTO.UserRegisterParam;
import org.example.crm.result.R;
import org.example.crm.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserRegisterController {
    @Autowired
    private UserRegisterService userRegisterService;

    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }

    @PostMapping("/register")
    public R register(@RequestBody UserRegisterParam param){
        if (param.getEmail() == null || param.getPassword() == null || param.getCode() == null) {
            return R.FAIL("参数缺失");
        }
        return userRegisterService.register(param.getEmail(), param.getPassword(), param.getCode());
    }

    // 获取验证码
    @GetMapping("/verifycode")
    public R getVerifyCode(@RequestParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return R.FAIL("邮箱不能为空");
        }
        return userRegisterService.sendVerifyCode(email);
    }
    //获取用户信息
    @GetMapping("/info")
    public R getUserInfo(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try{
            UserInfoDTO userInfo = userRegisterService.getUserInfo(uid);
            return R.OK(userInfo);
        }catch (Exception e){
            return R.FAIL("获取失败");
        }
    }
    //忘记密码
    @PostMapping("/pass")
    public R updatePassword(@RequestBody UserRegisterParam param){// 这个接口前端传来的参数和注册接口一样，所以复用一下，顺便说明
        if (param.getEmail() == null || param.getPassword() == null || param.getCode() == null) {
            return R.FAIL("参数缺失");
        }
        return userRegisterService.updatePassword(param.getEmail(), param.getPassword(), param.getCode());

    }
}

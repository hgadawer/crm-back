package org.example.crm.controller;

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
}

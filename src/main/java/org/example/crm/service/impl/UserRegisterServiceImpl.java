package org.example.crm.service.impl;

import org.example.crm.entity.User;
import org.example.crm.mapper.UserMapper;
import org.example.crm.result.R;
import org.example.crm.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入 BCryptPasswordEncoder
    @Override
    public R register(String email, String password, String code) {
        // 检查用户是否已存在
        User existingUser = userMapper.selectByEmail(email);
        if (existingUser != null) {
            return R.builder().code(10001).msg("该用户已经存在").info(null).build();
        }
        // 验证验证码
        String cachedCode = redisTemplate.opsForValue().get("VERIFY_CODE_" + email);
        if (!code.equals(cachedCode)) {
            return R.builder().code(10005).msg("验证码错误").info(null).build();
        }
        Date now = new Date();
        // 创建新用户（密码应加密存储）
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 密码加密， BCrypt
        user.setCreated(now);
        user.setUpdated(now);
        user.setStatus(1);

        userMapper.insert(user);

        // 删除使用过的验证码
        redisTemplate.delete("VERIFY_CODE_" + email);

        return R.OK("注册成功");
    }

    @Override
    public R sendVerifyCode(String email) {
        // 生成6位随机验证码
        String verifyCode = String.valueOf(100000 + new Random().nextInt(900000));

        // 发送邮件
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2083991845@qq.com"); // 你的邮箱
            message.setTo(email);
            message.setSubject("注册验证码");
            message.setText("您的验证码是: " + verifyCode + "，5分钟内有效");
            mailSender.send(message);

            // 存储验证码到Redis，设置5分钟过期
            redisTemplate.opsForValue().set(
                    "VERIFY_CODE_" + email,
                    verifyCode,
                    5,
                    TimeUnit.MINUTES
            );

            return R.OK("验证码发送成功");
        } catch (Exception e) {
            return R.FAIL("验证码发送失败");
        }
    }


}

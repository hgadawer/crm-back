package org.example.crm.controller;


import cn.hutool.jwt.JWTUtil;
import jakarta.annotation.Resource;
import org.example.crm.DTO.LoginRequest;
import org.example.crm.DTO.LoginResponse;
import org.example.crm.constant.Constant;
import org.example.crm.entity.User;
import org.example.crm.result.R;
import org.example.crm.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class AuthController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @PostMapping("/login")
    public R login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User user;
        // 1. 根据用户名加载用户详细信息
        try {
            user = (User) userServiceImpl.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return R.builder().code(HttpStatus.UNAUTHORIZED.value()).msg("用户不存在").info(null).build();
        }
        // 2. 验证密码是否匹配
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return R.builder().code(HttpStatus.UNAUTHORIZED.value()).msg("用户名或密码错误").info(null).build();
        }
        // 3. 构建 JWT 载荷
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        // 可扩展：可以加入用户ID、角色等信息，注意不要放入敏感数据
        payload.put("exp", System.currentTimeMillis() / 1000 + Constant.EXPIRATION); // UNIX 时间戳

        // 4. 利用 Hutool 生成 JWT（默认采用 HS256 算法）
        String token = JWTUtil.createToken(payload, Constant.SECRET.getBytes());

        // 5、将 token 存入 Redis，key 使用统一前缀
        //把这个token写入redis,token适合string或者hash结构进行存储，把user_id转成字符串存储
        redisTemplate.opsForHash().put(Constant.SECRET,String.valueOf(user.getId()),token);
        //设置该token的过期时间TTL
        redisTemplate.expire(Constant.SECRET, Constant.EXPIRATION, TimeUnit.SECONDS);

        // 6. 构造返回数据对象
        LoginResponse loginResponse = new LoginResponse(user.getId(), token);
        // 7. 返回 JWT 给前端
        return R.OK(loginResponse);
    }
}

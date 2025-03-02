package org.example.crm.service.impl;

import jakarta.annotation.Resource;
import org.example.crm.entity.User;
import org.example.crm.mapper.UserMapper;
import org.example.crm.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;
    @Override
    //前端通过email当做username传入
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 假设 user 中已经包含了角色信息并设置到 authorities 字段中
        return user;
    }
}

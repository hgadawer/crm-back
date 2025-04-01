package org.example.crm.service.impl;

import jakarta.annotation.Resource;
import org.example.crm.entity.User;
import org.example.crm.mapper.RoleMapper;
import org.example.crm.mapper.UserMapper;
import org.example.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.crm.entity.Role;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Override
    //前端通过email当做username传入
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //把该角色的role集合注入到该角色对象中
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        System.out.println(roles);
        user.setRoleList(roles);
        return user;
    }
}

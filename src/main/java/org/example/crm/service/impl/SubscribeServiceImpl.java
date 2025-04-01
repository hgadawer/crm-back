package org.example.crm.service.impl;

import org.example.crm.mapper.RoleMapper;
import org.example.crm.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscribeServiceImpl implements SubscribeService {
    @Autowired
    RoleMapper roleMapper;
    @Override
    public Map<String, Integer> getSubscribeInfo(Long uid) {
        Map<String, Integer> result = new HashMap<>();
        Integer version = roleMapper.selectVersionByUserId(uid);
        result.put("version", version.equals(1) ? 1 : 2);
        return result;
    }

    @Override
    public boolean updateSubscribeInfo(Long uid) {
        return roleMapper.updateSubscribe(uid)>0;
    }
}

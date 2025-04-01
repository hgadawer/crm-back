package org.example.crm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.crm.entity.Business;
import org.example.crm.entity.Notice;
import org.example.crm.mapper.BusinessMapper;
import org.example.crm.mapper.CustomerMapper;
import org.example.crm.mapper.NoticeMapper;
import org.example.crm.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    BusinessMapper businessMapper;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    NoticeMapper noticeMapper;

    @Override
    public boolean createBusiness(Business business) throws JsonProcessingException {

        Business existing= businessMapper.findByName(business.getName());
        if(existing!=null){
            return false;
        }
        Date now = new Date();
        business.setCreated(now);
        business.setUpdated(now);
        ObjectMapper objectMapper = new ObjectMapper();
        // 转换为 JSON 字符串
        String jsonStr = objectMapper.writeValueAsString(business.getProductlist());
        business.setProductlist(jsonStr);

        //产生通知
        Notice notice = new Notice();
        notice.setCreator(business.getCreator());
        notice.setContent("你创建了新业务: "+business.getName());
        notice.setCreated(now);
        notice.setUpdated(now);
        notice.setStatus(2);
        noticeMapper.insertSelective(notice);

        return businessMapper.insertSelective(business)==1;
    }

    @Override
    public Page<Business> getBusinessList(String name, Long uid, int pageNum, int pageSize) {
        // 调用 Mapper 查询全部满足条件的客户数据
        List<Business> fullList = businessMapper.queryBusinessList(name,uid);

        for(Business business : fullList) {
            business.setCname(customerMapper.selectByPrimaryKey(business.getCid()).getName());
        }
        int total = fullList.size();

        // 计算分页的起始和结束索引（页码从1开始）
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Business> pageList;
        if (fromIndex >= total) {
            pageList = new ArrayList<>();
        } else {
            pageList = fullList.subList(fromIndex, toIndex);
        }
        // 构造 Pageable 对象（注意：PageRequest 的页码从0开始，所以 pageNum - 1）
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        // 返回 PageImpl 对象
        return new PageImpl<>(pageList, pageable, total);
    }

    @Override
    public boolean deleteBusinesses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = businessMapper.deleteByIds(ids);
        return rows > 0;
    }

    @Override
    public Business queryBusinessInfo(Long id) {
        Business business = businessMapper.selectByPrimaryKey(id);
        business.setCname(customerMapper.selectByPrimaryKey(business.getCid()).getName());
        return business;
    }

    @Override
    public boolean updateBusiness(Business business) throws JsonProcessingException {
        // 调用 Mapper 方法进行更新，返回受影响的行数
        Date now = new Date();
        business.setUpdated(now);
        // 转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(business.getProductlist());
        business.setProductlist(jsonStr);
        int rows = businessMapper.updateByPrimaryKeySelective(business);
        Business temp = businessMapper.selectByPrimaryKey(business.getId());
        //产生通知
        Notice notice = new Notice();
        notice.setCreator(temp.getCreator());
        notice.setContent("你更新了业务: "+temp.getName());
        notice.setCreated(now);
        notice.setUpdated(now);
        notice.setStatus(2);
        noticeMapper.insertSelective(notice);

        return rows > 0;
    }
}

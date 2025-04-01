package org.example.crm.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.example.crm.entity.Business;

import org.springframework.data.domain.Page;

import java.util.List;

public interface BusinessService {
    boolean createBusiness(Business business) throws JsonProcessingException;

    Page<Business> getBusinessList(String name, Long uid, int pageNum, int pageSize);

    boolean deleteBusinesses(List<Long> ids);


    Business queryBusinessInfo(Long id);

    boolean updateBusiness(Business business) throws JsonProcessingException;
}

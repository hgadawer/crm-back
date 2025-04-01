package org.example.crm.service.impl;

import org.example.crm.mapper.ContractMapper;
import org.example.crm.mapper.CustomerMapper;
import org.example.crm.mapper.ProductMapper;
import org.example.crm.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ProductMapper productMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public Map<String, Object> getSumData(Integer daysRange,Long uid) {
        Map<String, Object> result = new HashMap<>();
        // 1. 获取基础统计数据
        int customerCount = customerMapper.countAllCustomers(uid);

        int contractCount = contractMapper.countAllContracts(uid);
        int productCount = productMapper.countAllProducts(uid);
        BigDecimal contractAmount = contractMapper.sumContractAmount(uid);
        result.put("customers", customerCount);
        result.put("contracts", contractCount);
        result.put("contractAmount", contractAmount);
        result.put("products", productCount);
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        List<BigDecimal> amount = new ArrayList<>();
        for (int i = 0; i < daysRange; i++) {
            dates.add(today);
            Date temp = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            amount.add(contractMapper.queryAmountByDate(temp,uid));
            today = today.plusDays(1);
        }
        result.put("date",dates);
        result.put("amount",amount);

        List<Map<String, Object>> customerIndustry = customerMapper.getCustomerIndustryDistribution(uid);
        result.put("customerIndustry",customerIndustry);

        return result;
    }
}

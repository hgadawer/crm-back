package org.example.crm.service.impl;

import org.apache.poi.xssf.usermodel.*;
import org.example.crm.DTO.CustomerIdAndName;
import org.example.crm.entity.Customer;
import org.example.crm.mapper.CustomerMapper;
import org.example.crm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerMapper customerMapper;

    @Override
    public Page<Customer> getCustomerList(String name, String source, String industry, String level, String status, int pageNum, int pageSize) {
        // 调用 Mapper 查询全部满足条件的客户数据
        List<Customer> fullList = customerMapper.queryCustomerList(name, source, industry, level, status);
        int total = fullList.size();

        // 计算分页的起始和结束索引（页码从1开始）
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Customer> pageList;
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
    public Customer queryCustomerInfo(Long id) {

        return customerMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean createCustomer(Customer customer) {
        // 根据客户名称查询是否已存在相同客户
        Customer existing = customerMapper.findByName(customer.getName());
        if (existing != null) {
            return false;
        }
        Date now = new Date();
        customer.setCreated(now);
        customer.setUpdated(now);
        // 插入客户数据
        int rows = customerMapper.insertSelective(customer);
        return rows > 0;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        // 调用 Mapper 方法进行更新，返回受影响的行数
        Date now = new Date();
        customer.setUpdated(now);
        int rows = customerMapper.updateByPrimaryKeySelective(customer);
        return rows > 0;
    }

    @Override
    public boolean deleteCustomers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = customerMapper.deleteByIds(ids);
        return rows > 0;
    }

    @Override
    public byte[] exportCustomersToExcel() throws IOException {
        List<Customer> customers = customerMapper.queryCustomerList(null,null,null,null,null);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("客户信息");

            String[] headers = {"ID", "姓名", "邮箱", "电话", "创建时间"};
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                XSSFCellStyle style = workbook.createCellStyle();
                XSSFFont font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (Customer customer : customers) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getPhone());
                row.createCell(4).setCellValue(
                        customer.getCreated() != null ?
                                customer.getCreated().toString() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public List<CustomerIdAndName> getAllCustomerIdsAndNames() {

        return  customerMapper.selectAllCustomerIdsAndNames();
    }
}

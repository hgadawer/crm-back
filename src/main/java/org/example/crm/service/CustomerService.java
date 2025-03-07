package org.example.crm.service;

import org.example.crm.DTO.CustomerIdAndName;
import org.example.crm.entity.Customer;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface CustomerService {
    /**
     * 根据查询条件分页获取客户列表
     * @param name 客户名称
     * @param source 客户来源
     * @param industry 行业
     * @param level 级别
     * @param status 状态
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @return 包含总记录数 total 和客户列表 list 的 Map
     */
    Page<Customer> getCustomerList(String name, String source, String industry, String level, String status, int pageNum, int pageSize);

    /**
     * 根据客户id获取对应客户的信息
     * @param id
     * @return
     */
    Customer queryCustomerInfo(Long id);

    /**
     * 新建客户，若客户名称已存在则返回 false
     */
    boolean createCustomer(Customer customer);

    boolean updateCustomer(Customer customer);

    /**
     * 根据id列表，批量删除客户
     * @param ids
     * @return
     */
    boolean deleteCustomers(List<Long> ids);

    byte[] exportCustomersToExcel() throws IOException;

    List<CustomerIdAndName> getAllCustomerIdsAndNames();
}

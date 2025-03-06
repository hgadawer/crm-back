package org.example.crm.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.crm.entity.Customer;

import java.util.List;

/**
* @author 20839
* @description 针对表【customer】的数据库操作Mapper
* @createDate 2025-03-02 22:53:37
* @Entity .org.example.crm.entity.Customer
*/
@Mapper
public interface CustomerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    List<Customer> queryCustomerList(
            @Param("name") String name,
            @Param("source") String source,
            @Param("industry") String industry,
            @Param("level") String level,
            @Param("status") String status
    );

    Customer findByName(@Param("name") String name);

    int deleteByIds(@Param("ids") List<Long> ids);

}

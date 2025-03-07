package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.crm.entity.Business;

import java.util.List;

/**
* @author 20839
* @description 针对表【business】的数据库操作Mapper
* @createDate 2025-03-07 12:16:51
* @Entity .org.example.crm.entity.Business
*/
@Mapper
public interface BusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Business record);

    int insertSelective(Business record);

    Business selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Business record);

    int updateByPrimaryKey(Business record);

    List<Business> queryBusinessList(String name);

    int deleteByIds(@Param("ids") List<Long> ids);
    Business findByName(String name);
}

package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.crm.entity.Role;

import java.util.List;

/**
* @author 20839
* @description 针对表【role】的数据库操作Mapper
* @createDate 2025-03-31 09:09:58
* @Entity .org.example.crm.entity.Role
*/
@Mapper
public interface RoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> selectByUserId(Long id);
}

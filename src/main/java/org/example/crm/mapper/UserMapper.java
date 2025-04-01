package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.crm.entity.User;

/**
* @author 20839
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-03-02 19:54:28
* @Entity org.example.crm.entity.User
*/
@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByEmail(String email);

    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);


}

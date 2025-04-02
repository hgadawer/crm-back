package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.crm.entity.MailConfig;

/**
* @author 20839
* @description 针对表【mail_config】的数据库操作Mapper
* @createDate 2025-04-01 23:28:01
* @Entity .org.example.crm.entity.MailConfig
*/
@Mapper
public interface MailConfigMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MailConfig record);

    int insertSelective(MailConfig record);

    MailConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MailConfig record);

    int updateByPrimaryKey(MailConfig record);

    MailConfig selectByCreatorId(Long uid);

    boolean updateStatusById(Long id, int status);
}

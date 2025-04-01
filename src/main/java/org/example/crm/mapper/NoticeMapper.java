package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.crm.entity.Notice;

import java.util.List;

/**
* @author 20839
* @description 针对表【notice】的数据库操作Mapper
* @createDate 2025-04-01 11:07:04
* @Entity .org.example.crm.entity.Notice
*/
@Mapper
public interface NoticeMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Notice record);

    int insertSelective(Notice record);

    Notice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Notice record);

    int updateByPrimaryKey(Notice record);

    Long getNoticeCount(Long uid);

    List<Notice> selectNoticeList(Long uid);

    int updateStatusById(@Param("id")Long id, @Param("notice") Notice notice);

    int deleteByIds(@Param("ids") List<Long> ids);
}

package org.example.crm.service;

import org.example.crm.entity.Notice;

import java.util.List;

public interface NoticeService {
    Long getNoticeCount(Long uid);

    List<Notice> getNoticeList(Long uid);

    boolean updateStatusById(Long id);

    boolean deleteStatusByIds(List<Long> ids);
}

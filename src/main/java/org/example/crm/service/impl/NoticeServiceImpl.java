package org.example.crm.service.impl;

import org.example.crm.entity.Notice;
import org.example.crm.mapper.NoticeMapper;
import org.example.crm.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;
    @Override
    public Long getNoticeCount(Long uid) {
        return noticeMapper.getNoticeCount(uid);
    }

    @Override
    public List<Notice> getNoticeList(Long uid) {
        return noticeMapper.selectNoticeList(uid);
    }

    @Override
    public boolean updateStatusById(Long id) {
        Date now = new Date();
        Notice notice = new Notice();
        notice.setStatus(1);
        notice.setUpdated(now);
        return noticeMapper.updateStatusById(id,notice)>0;
    }

    @Override
    public boolean deleteStatusByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = noticeMapper.deleteByIds(ids);
        return rows > 0;
    }
}

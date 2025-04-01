package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.DeleteNoticeDTO;
import org.example.crm.DTO.UpdateNoticeDTO;
import org.example.crm.entity.Notice;
import org.example.crm.result.R;
import org.example.crm.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }
    /**
     * 获取未读通知数量
     */
    @GetMapping("/count")
    public R getNoticeCount(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try {
            Long count = noticeService.getNoticeCount(uid);
            return R.OK(count);
        }catch (Exception e){
            return R.FAIL(e);
        }
    }
    @GetMapping("/list")
    public R getNoticeList(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try {
            List<Notice> list = noticeService.getNoticeList(uid);
            return R.OK(list);
        }catch (Exception e){
            return R.FAIL(e);
        }
    }

    @PutMapping("/update")
    public R updateNotice(@RequestBody UpdateNoticeDTO updateNoticeDTO){
        try {
            Long id = updateNoticeDTO.getId();
            boolean success = noticeService.updateStatusById(id);
            if(success) {
                return R.OK("更新成功");
            }else
                return R.FAIL("更新失败");
        }catch (Exception e){
            return R.FAIL(e);
        }
    }
    @DeleteMapping("/delete")
    public R deleteAllNotice(@RequestBody DeleteNoticeDTO deleteNoticeDTO){
        try {
            List<Long> ids = deleteNoticeDTO.getIds();
            boolean success = noticeService.deleteStatusByIds(ids);
            if(success) {
                return R.OK("删除成功");
            }else
                return R.FAIL("删除失败");
        }catch (Exception e){
            return R.FAIL(e);
        }
    }
}

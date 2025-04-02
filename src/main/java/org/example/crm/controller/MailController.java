package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.MailConfigStatusDTO;
import org.example.crm.entity.MailConfig;
import org.example.crm.result.R;
import org.example.crm.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/config")
public class MailController {

    @Autowired
    MailService mailService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }
    @PreAuthorize(value = "hasRole('root')")
    @PutMapping("/save")
    public R saveConfig(@RequestBody MailConfig mailConfig, HttpServletRequest request){
        mailConfig.setCreator(getCurrentId(request));
        Date now = new Date();
        mailConfig.setCreated(now);
        mailConfig.setUpdated(now);
        mailConfig.setStatus(mailConfig.getStatus());
        return mailService.saveConfig(mailConfig);
    }
    @PreAuthorize(value = "hasRole('root')")
    @GetMapping("/info")
    public R getConfigInfo(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try{
             return mailService.getConfigInfo(uid);
        }catch (Exception e){
            return R.FAIL("获取失败"+e);
        }

    }
    @PreAuthorize(value = "hasRole('root')")
    @DeleteMapping("/delete")
    public R deleteConfig(@RequestBody Integer id){
        try {
            boolean success = mailService.deleteConfig(Long.valueOf(String.valueOf(id)));
            if(success){
                return R.OK("删除成功");
            }else
                return R.FAIL("删除失败");
        }catch (Exception e){
            return R.FAIL(e);
        }
    }
    @PreAuthorize(value = "hasRole('root')")
    @PutMapping("/status")
    public R updateStatus(@RequestBody MailConfigStatusDTO mailConfigStatusDTO){
        try {
            boolean success = mailService.updateStatus(mailConfigStatusDTO.getId(),mailConfigStatusDTO.getStatus());
            if(success){
                return R.OK("更新状态成功");
            }else
                return R.FAIL("更新状态失败");
        }catch (Exception e){
            return R.FAIL(e);
        }
    }

    /**
     * 检查邮件有效性
     * @return
     */
    @PreAuthorize(value = "hasRole('root')")
    @GetMapping("/check")
    public R checkConfig(HttpServletRequest request) throws Exception {
        Long uid = getCurrentId(request);
        if(mailService.getConfigInfo(uid)==null){
            return R.FAIL("邮件服务未配置");
        }

        try {
            boolean success = mailService.checkConfig(uid);
            if(success){
                return R.OK("该邮件配置有效");
            }else
                return R.FAIL("该邮件配置无效");
        }catch (Exception e){
            return R.FAIL(e);
        }

    }
}

package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.result.R;
import org.example.crm.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    @Autowired
    SubscribeService subscribeService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }

    /**
     * 返回当前用户的系统使用版本：1、免费版  2、专业版
     * @param request
     * @return
     */
    @GetMapping("/info")
    public R getSubscribeInfo(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try{
            return R.OK(subscribeService.getSubscribeInfo(uid));
        }catch (Exception e){
            return R.FAIL(e);
        }
    }

    @PostMapping("/pay")
    public R paySubscribe(HttpServletRequest request){
        Long uid = getCurrentId(request);
        try{
            return R.OK(subscribeService.updateSubscribeInfo(uid)?"支付成功，当前专业版":"支付失败");
        }catch (Exception e){
            return R.FAIL(e);
        }

    }
}

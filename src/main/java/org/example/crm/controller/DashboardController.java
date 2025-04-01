package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.result.R;
import org.example.crm.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }

    @PreAuthorize(value = "hasRole('root')")
    @GetMapping("/sum")
    public R GetSumSuggestion(@RequestParam(defaultValue = "7") Integer daysRange,HttpServletRequest request){
        Map<String, Object> summaryData = dashboardService.getSumData(daysRange,getCurrentId(request));
        return R.OK(summaryData);
    }

}

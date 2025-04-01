package org.example.crm.controller;

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

    @PreAuthorize(value = "hasRole('root')")
    @GetMapping("/sum")
    public R GetSumSuggestion(@RequestParam(defaultValue = "7") Integer daysRange){
        Map<String, Object> summaryData = dashboardService.getSumData(daysRange);
        return R.OK(summaryData);
    }

}

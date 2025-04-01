package org.example.crm.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getSumData(Integer daysRange);
}

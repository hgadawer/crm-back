package org.example.crm.service;
import java.util.Map;
public interface SubscribeService {
    Map<String, Integer>  getSubscribeInfo(Long uid);

    boolean updateSubscribeInfo(Long uid);
}

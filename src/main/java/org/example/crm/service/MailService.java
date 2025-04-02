package org.example.crm.service;

import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import org.example.crm.entity.MailConfig;
import org.example.crm.result.R;

public interface MailService {
    R saveConfig(MailConfig mailConfig);

    R getConfigInfo(Long uid) throws Exception;

    boolean deleteConfig(Long id);

    boolean updateStatus(Long id, int status);

    boolean checkConfig(Long uid) throws MessagingException;
}

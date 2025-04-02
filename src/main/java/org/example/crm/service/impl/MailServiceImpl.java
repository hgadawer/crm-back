package org.example.crm.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.*;
import org.example.crm.entity.MailConfig;
import org.example.crm.mapper.MailConfigMapper;
import org.example.crm.result.R;
import org.example.crm.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    MailConfigMapper mailConfigMapper;
    @Resource
    RedisTemplate<String,String> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "mail:config:check:";
    private static final long CACHE_EXPIRE_HOURS = 1; // 缓存过期时间1小时

    @Override
    public R saveConfig(MailConfig mailConfig) {
        if(mailConfigMapper.selectByPrimaryKey(mailConfig.getId())!= null){
            mailConfigMapper.updateByPrimaryKeySelective(mailConfig);
        }else {
            mailConfigMapper.insertSelective(mailConfig);
        }
        deleteCatch(mailConfig.getCreator());
        return R.OK(mailConfig);

    }

    @Override
    public R getConfigInfo(Long uid) {
        return R.OK(mailConfigMapper.selectByCreatorId(uid));
    }

    @Override
    public boolean deleteConfig(Long id) {
        deleteCatch(mailConfigMapper.selectByPrimaryKey(id).getCreator());
        return mailConfigMapper.deleteByPrimaryKey(id)>0;
    }

    @Override
    public boolean updateStatus(Long id, int status) {
        deleteCatch(mailConfigMapper.selectByPrimaryKey(id).getCreator());
        return mailConfigMapper.updateStatusById(id,status);
    }
    /**
     * 检查邮件配置有效性（仅验证连接）
     * @param uid 用户ID
     * @return true表示配置有效，false表示无效
     */
    @Override
    public boolean checkConfig(Long uid) {
        // 1. 先查Redis缓存
        String redisKey = REDIS_KEY_PREFIX + uid;
        String cachedResult = redisTemplate.opsForValue().get(redisKey);
        if (cachedResult!=null && cachedResult.equals("true")) {
            return true;
        }
        MailConfig mailConfig = mailConfigMapper.selectByCreatorId(uid);
        boolean checkResult = performSmtpCheck(mailConfig);
        cacheResult(redisKey, checkResult);
        return checkResult;
    }
    private boolean performSmtpCheck(MailConfig mailConfig){
        if(mailConfig == null||mailConfig.getStatus()  != 1){return false;}
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getStmp());
        mailSender.setPort(mailConfig.getPort());
        mailSender.setUsername(mailConfig.getEmail());
        mailSender.setPassword(mailConfig.getAuthCode());

        Properties props = new Properties();
        props.put("mail.smtp.auth",  "true");
        props.put("mail.smtp.starttls.enable",  "true");
        props.put("mail.smtp.timeout",  "30000"); // 30秒连接超时
        props.put("mail.smtp.connectiontimeout",  "30000"); // 30秒连接超时
        Session session = Session.getInstance(props);
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(mailConfig.getStmp(), mailConfig.getPort(),
                    mailConfig.getEmail(), mailConfig.getAuthCode());
            return true; // 连接成功
        } catch (AuthenticationFailedException e) {
            // 认证失败（用户名/密码错误）
            return false;
        } catch (MessagingException e) {
            // 其他连接问题（网络问题、服务器不可达等）
            return false;
        }

    }

    private void cacheResult(String key, boolean result) {
        try {
            String res = "false";
            if(result) res = "true";
            redisTemplate.opsForValue().set(
                    key,
                    res,
                    CACHE_EXPIRE_HOURS,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            // Redis操作异常不影响主流程
            System.out.println("Cache  mail config check result failed"+e);
        }
    }

    /**
     * 每次删除/更新配置信息/更新状态，都需要删除已存的邮件是否有效缓存
     */
    private void deleteCatch(Long uid){
        String key =  REDIS_KEY_PREFIX + uid;
        redisTemplate.delete(key);
    }

}

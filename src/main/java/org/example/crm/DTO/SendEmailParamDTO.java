package org.example.crm.DTO;

import lombok.Data;

@Data
public class SendEmailParamDTO {
    private String receiver;  // 收件人邮箱
    private String subject;   // 邮件主题
    private String content;   // 邮件内容
    private String attachment; // 附件URL
}

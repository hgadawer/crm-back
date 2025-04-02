package org.example.crm.entity;

import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName mail_config
 */
@Data
public class MailConfig {
    /**
     * 编号
     */
    private Long id;

    /**
     * ip地址或域名
     */
    private String stmp;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 授权码
     */
    private String authCode;

    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 服务状态
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;
}
package org.example.crm.entity;

import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName notice
 */
@Data
public class Notice {
    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知内容
     */
    private String content;

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

    /**
     * 是否已读：1表示已读，2表示未读
     */
    private int status;
}
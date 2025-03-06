package org.example.crm.entity;

import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName customer
 */
@Data
public class Customer {
    /**
     * 编号
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 来源
     */
    private String source;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所在行业
     */
    private String industry;

    /**
     * 级别
     */
    private String level;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 地区
     */
    private String region;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 成交状态
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
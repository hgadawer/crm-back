package org.example.crm.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName business
 */
@Data
public class Business {
    /**
     * 编号
     */
    private Long id;

    /**
     * 业务名称
     */
    private String name;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date overTime;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 客户编号
     */
    private Long cid;

    /**
     * 客户名称
     */
    private String cname;

    /**
     * 产品编号和数量
     */

    private Object productlist;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 
     */
    private Date created;

    /**
     * 
     */
    private Date updated;
}
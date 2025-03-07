package org.example.crm.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName product
 */
@Data
public class Product {
    /**
     * 编号
     */
    private Long id;

    /**
     * 套餐
     */
    private String name;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 单位
     */
    private String unit;

    /**
     * 名称
     */
    private String code;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态，1-上架，2-下架
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
     * 
     */
    private Date updated;
}
package org.example.crm.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 合同表
 * @TableName contract
 */
@Data
public class Contract {
    /**
     * 合同ID
     */
    private Long id;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 签订日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date signDate;

    /**
     * 到期日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date expireDate;

    /**
     * 合同金额
     */
    private BigDecimal amount;

    /**
     * 合同描述
     */
    private String description;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态：1已签，2未签
     */
    private Integer status;

    /**
     * 创建人ID
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
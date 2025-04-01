package org.example.crm.entity;

import lombok.Data;

/**
 * 
 * @TableName role
 */
@Data
public class Role {
    /**
     * 
     */
    private Long id;

    /**
     * 角色标识
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;
}
package org.example.crm.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName user
 */
public class User implements UserDetails {
    /**
     * 编号
     */
    private Long id;

    /**
     * 登录邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态，1-正常，2-注销
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    private List<Role> roleList;
    /**
     * 账户是否没有过期，0已过期 1正常
     */
    private boolean accountNoExpired;

    /**
     * 账号是否没有锁定，0已锁定 1正常
     */
    private boolean accountNoLocked;

    /**
     * 密码是否没有过期，0已过期 1正常
     */
    private boolean credentialsNoExpired;

    /**
     * 账号是否启用，0禁用 1启用
     */
    private boolean accountEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(Role role : this.roleList){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
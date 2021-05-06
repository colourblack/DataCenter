package com.yingf.domain.entity;

import java.util.Date;

/**
 * @author fangjunjin
 */
public class SysAccountInfo {
    private Long userId;

    private String accountName;

    private String password;

    private Integer isDeleted;

    private Date createdTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "SysAccountInfo{" +
                "userId=" + userId +
                ", accountName='" + accountName + '\'' +
                ", password='" + password + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdTime=" + createdTime +
                '}';
    }
}
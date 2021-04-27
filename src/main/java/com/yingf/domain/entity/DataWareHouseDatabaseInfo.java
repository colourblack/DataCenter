package com.yingf.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yingf Fangjunjin
 */
public class DataWareHouseDatabaseInfo implements Serializable {
    private Long id;

    private String databaseIp;

    private Integer databasePort;

    private String databaseType;

    private String databaseName;

    private String databaseUsername;

    private String databasePassword;

    private String dataModelName;

    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatabaseIp() {
        return databaseIp;
    }

    public void setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp == null ? null : databaseIp.trim();
    }

    public Integer getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(Integer databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType == null ? null : databaseType.trim();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName == null ? null : databaseName.trim();
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername == null ? null : databaseUsername.trim();
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword == null ? null : databasePassword.trim();
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName == null ? null : dataModelName.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", databaseIp=").append(databaseIp);
        sb.append(", databasePort=").append(databasePort);
        sb.append(", databaseType=").append(databaseType);
        sb.append(", databaseName=").append(databaseName);
        sb.append(", databaseUsername=").append(databaseUsername);
        sb.append(", databasePassword=").append(databasePassword);
        sb.append(", dataModelName=").append(dataModelName);
        sb.append(", createdTime=").append(createdTime);
        sb.append("]");
        return sb.toString();
    }
}
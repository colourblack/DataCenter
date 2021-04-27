package com.yingf.domain.entity;

import java.util.Date;

/**
 * @author yingf Fangjunjin
 */
public class DataWareHouseInfo {
    private Long id;

    private Long generatorId;

    private String generatorName;

    private String dataModelName;

    private String dataSourceType;

    private Long fileId;

    private Long databaseId;

    private String tableList;

    private Date createdTime;

    private Integer isStored;

    private Integer isUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(Long generatorId) {
        this.generatorId = generatorId;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName == null ? null : generatorName.trim();
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName == null ? null : dataModelName.trim();
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType == null ? null : dataSourceType.trim();
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public String getTableList() {
        return tableList;
    }

    public void setTableList(String tableList) {
        this.tableList = tableList == null ? null : tableList.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getIsStored() {
        return isStored;
    }

    public void setIsStored(Integer isStored) {
        this.isStored = isStored ;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", generatorId=" + generatorId +
                ", generatorName=" + generatorName +
                ", dataModelName=" + dataModelName +
                ", dataSourceType=" + dataSourceType +
                ", fileId=" + fileId +
                ", databaseId=" + databaseId +
                ", tableName=" + tableList +
                ", createdTime=" + createdTime +
                ", isStored=" + isStored +
                ", isUsed=" + isUsed +
                "]";
    }
}
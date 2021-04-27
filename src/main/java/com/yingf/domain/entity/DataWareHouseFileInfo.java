package com.yingf.domain.entity;

import java.util.Date;

/**
 * @author yingf Fangjunjin
 */
public class DataWareHouseFileInfo {
    private Long id;

    private Long fileId;

    private String fileName;

    private String filePath;

    private String dataModelName;

    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId ;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
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
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", fileId=" + fileId +
                ", fileName=" + fileName +
                ", filePath=" + filePath +
                ", dataModelName=" + dataModelName +
                ", createdTime=" + createdTime +
                "]";
    }
}
package com.yingf.domain.vo;

/**
 * @author yingf Fangjunjin
 * @Description 文件上传过程中, 用于检测文件上传状态的传输对象
 * @Date 2021/3/9
 */
public class CheckFileUpLoadVO {

    private String status;

    private Long fileId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "CheckFileUpLoadVO{" +
                "status='" + status + '\'' +
                ", fileId=" + fileId +
                '}';
    }
}

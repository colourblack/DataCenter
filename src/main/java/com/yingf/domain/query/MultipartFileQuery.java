package com.yingf.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author yingf Fangjunjin
 * @Date 2021/3/9
 */
@ApiModel(value = "MultipartFileQuery", description = "文件上传(分块)参数信息")
public class MultipartFileQuery implements Serializable {

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("文件Md5值")
    private String md5;

    @ApiModelProperty("文件唯一Id")
    private Long uuid;

    @ApiModelProperty("文件名")
    private String name;

    @ApiModelProperty("文件大小")
    private String size;

    @ApiModelProperty("文件总分片数")
    private Integer total;

    @ApiModelProperty("文件当前分片")
    private Integer index;

    @ApiModelProperty("此次执行的操作")
    private String action;

    @ApiModelProperty("当前分片的md5值")
    private String partMd5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPartMd5() {
        return partMd5;
    }

    public void setPartMd5(String partMd5) {
        this.partMd5 = partMd5;
    }

    @Override
    public String toString() {
        return "MultipartFileParam{" +
                "md5='" + md5 + '\'' +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", total=" + total +
                ", index=" + index +
                ", action='" + action + '\'' +
                ", partMd5='" + partMd5 + '\'' +
                '}';
    }
}

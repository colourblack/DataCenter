package com.yingf.domain.vo.original;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 指定sheet的预览信息
 * @Date 2021/3/11
 */
@ApiModel(value = "Sheet的预览信息")
public class SheetDataPreviewVO implements Serializable {

    @ApiModelProperty(value = "总记录数")
    private Integer totalRecord;

    @ApiModelProperty(value = "无法正确解析的记录行数")
    private Integer errorRecord;

    @ApiModelProperty(value = "记录详情")
    private List<Map<String, Object>> record;

    @ApiModelProperty(value = "表头")
    private List<String> header;


    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getErrorRecord() {
        return errorRecord;
    }

    public void setErrorRecord(Integer errorRecord) {
        this.errorRecord = errorRecord;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<Map<String, Object>> getRecord() {
        return record;
    }

    public void setRecord(List<Map<String, Object>> record) {
        this.record = record;
    }

    @Override
    public String toString() {
        return "SheetDataPreviewVO{" +
                "totalRecord=" + totalRecord +
                ", errorRecord=" + errorRecord +
                ", record=" + record +
                ", header=" + header +
                '}';
    }
}

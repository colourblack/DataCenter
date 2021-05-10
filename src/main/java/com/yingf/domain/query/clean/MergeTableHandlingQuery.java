package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 10:35 AM
 */
@ApiModel(value = "MergeTableHandlingQuery", description = "多表关联参数")
public class MergeTableHandlingQuery extends AbstractCleaningQuery {

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("左表关联字段名")
    private List<String> leftOn;

    @ApiModelProperty("右表关联字段名")
    private List<String> rightOn;

    @ApiModelProperty("关联方式")
    private String how;

    @ApiModelProperty("关联类型")
    private String connType;

    @ApiModelProperty("关联子表保留字段")
    List<String> maintainCol;

    @ApiModelProperty("关联子表保留列字段名转换")
    Map<String, String> reviseName;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getLeftOn() {
        return leftOn;
    }

    public void setLeftOn(List<String> leftOn) {
        this.leftOn = leftOn;
    }

    public List<String> getRightOn() {
        return rightOn;
    }

    public void setRightOn(List<String> rightOn) {
        this.rightOn = rightOn;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }

    public String getConnType() {
        return connType;
    }

    public void setConnType(String connType) {
        this.connType = connType;
    }

    public List<String> getMaintainCol() {
        return maintainCol;
    }

    public void setMaintainCol(List<String> maintainCol) {
        this.maintainCol = maintainCol;
    }

    public Map<String, String> getReviseName() {
        return reviseName;
    }

    public void setReviseName(Map<String, String> reviseName) {
        this.reviseName = reviseName;
    }

    @Override
    public String toString() {
        return "MergeTableHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", leftOn=" + leftOn.toString() +
                ", rightOn=" + rightOn.toString() +
                ", how='" + how + '\'' +
                ", connType='" + connType + '\'' +
                ", maintainCol=" + maintainCol.toString() +
                ", reviseName=" + reviseName.toString() +
                '}';
    }
}

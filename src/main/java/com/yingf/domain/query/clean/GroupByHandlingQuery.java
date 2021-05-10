package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 10:16 AM
 */
@ApiModel(value = "GroupByHandlingQuery", description = "聚合运算")
public class GroupByHandlingQuery extends AbstractCleaningQuery{

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("聚合字段")
    private List<String> cols;

    @ApiModelProperty("聚合方法")
    private String opr;

    @ApiModelProperty("影响行列")
    private int axis;

    @ApiModelProperty("新字段名称")
    private String newName;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }

    public String getOpr() {
        return opr;
    }

    public void setOpr(String opr) {
        this.opr = opr;
    }

    public int getAxis() {
        return axis;
    }

    public void setAxis(int axis) {
        this.axis = axis;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return "GroupByHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", cols=" + cols.toString() +
                ", opr='" + opr + '\'' +
                ", axis=" + axis +
                ", newName='" + newName + '\'' +
                '}';
    }
}

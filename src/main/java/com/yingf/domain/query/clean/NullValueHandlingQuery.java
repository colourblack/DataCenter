package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yingf Fangjunjin
 * @Description 空值处理请求参数
 * @Date 2021/3/16
 */
@ApiModel(value = "NullValueHandlingQuery", description = "空值处理参数")
public class NullValueHandlingQuery extends AbstractCleaningQuery {

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("处理方式")
    private String nullProcessType;

    @ApiModelProperty("填充值")
    private String fillValue;

    @ApiModelProperty("字段名")
    private List<String> col;

    @ApiModelProperty("记录号")
    private List<Integer> row;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getNullProcessType() {
        return nullProcessType;
    }

    public void setNullProcessType(String handleType) {
        this.nullProcessType = handleType;
    }

    public String getFillValue() {
        return fillValue;
    }

    public void setFillValue(String fillValue) {
        this.fillValue = fillValue;
    }

    public List<String> getCol() {
        return col;
    }

    public void setCol(List<String> col) {
        this.col = col;
    }

    public List<Integer> getRow() {
        return row;
    }

    public void setRow(List<Integer> row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "NullValueHandlingQuery{" +
                "userId='" + super.getUserId() + '\'' +
                ", dataModelName='" + super.getDataModelName() + '\'' +
                ", tableName='" + super.getTableName() + '\'' +
                ", saveAs='" + super.getSaveAs() + '\'' +
                ", processor='" + processor + '\'' +
                ", nullProcessType='" + nullProcessType + '\'' +
                ", fillValue='" + fillValue + '\'' +
                ", col=" + col.toString() +
                ", row=" + row.toString() +
                '}';
    }
}

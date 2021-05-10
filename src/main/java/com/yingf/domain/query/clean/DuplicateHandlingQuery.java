package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 4:19 PM
 */
@ApiModel(value = "DuplicateProcessorQuery", description = "重复值处理参数")
public class DuplicateHandlingQuery extends AbstractCleaningQuery {

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("字段名称")
    List<String> colName;

    public DuplicateHandlingQuery() {

    }

    public DuplicateHandlingQuery(String processor, List<String> colName) {
        this.processor = processor;
        this.colName = colName;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getColName() {
        return colName;
    }

    public void setColName(List<String> colName) {
        this.colName = colName;
    }

    @Override
    public String toString() {
        return "DuplicateProcessorQuery{" +
                "processor='" + processor + '\'' +
                ", colName=" + colName +
                '}';
    }
}

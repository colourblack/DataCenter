package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 5:45 PM
 */
@ApiModel(value = "DelColArgsHandlingQuery", description = "字段删除的参数")
public class DelColHandlingQuery extends AbstractCleaningQuery{


    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("需要删除的字段名称")
    private List<String> colData;

    public DelColHandlingQuery() {

    }

    public DelColHandlingQuery(String processor, List<String> colData) {
        this.processor = processor;
        this.colData = colData;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getColData() {
        return colData;
    }

    public void setColData(List<String> colData) {
        this.colData = colData;
    }
}

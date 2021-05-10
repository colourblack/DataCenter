package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 10:09 AM
 */
@ApiModel(value = "FilterColByConditionQuery", description = "根据条件对字段的值进行筛选")
public class FilterColByConditionQuery extends AbstractCleaningQuery{

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("条件参数")
    private List<AlterColByConditionHandlingQuery.ConditionArgs> condition;

    public List<AlterColByConditionHandlingQuery.ConditionArgs> getCondition() {
        return condition;
    }

    public void setCondition(List<AlterColByConditionHandlingQuery.ConditionArgs> condition) {
        this.condition = condition;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    @Override
    public String toString() {
        return "FilterColByConditionQuery{" +
                "processor='" + processor + '\'' +
                ", condition=" + condition.toString() +
                '}';
    }
}

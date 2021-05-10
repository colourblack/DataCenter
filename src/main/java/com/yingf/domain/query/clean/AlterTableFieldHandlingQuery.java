package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 9:25 AM
 */
@ApiModel(value = "AlterTableFieldHandlingQuery", description = "执行过滤方案参数")
public class AlterTableFieldHandlingQuery extends AbstractCleaningQuery {

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("新的字段顺序")
    private List<String> order;

    @ApiModelProperty("新的字段名")
    private Map<String, String> rename;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public Map<String, String> getRename() {
        return rename;
    }

    public void setRename(Map<String, String> rename) {
        this.rename = rename;
    }

    @Override
    public String toString() {
        return "AlterTableFieldHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", order=" + order.toString() +
                ", rename=" + rename.toString() +
                '}';
    }
}

package com.yingf.domain.dto;

import com.yingf.domain.query.clean.AlterRowHandlingQuery;
import com.yingf.domain.query.clean.FilterColByConditionQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @Description 数据中心 - 清洗分区
 *              根据条件对字段的值进行筛选, 用于向python端发送清洗任务信息
 * @since 5/8/21 3:44 PM
 */
@ApiModel(value = "FilterColByConditionDTO", description = "根据条件对字段的值进行筛选 - DTO")
public class FilterColByConditionDTO extends FilterColByConditionQuery {

    @ApiModelProperty("请求python的方法")
    private String handleType;

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getHandleType() {
        return handleType;
    }

    @Override
    public String toString() {
        return "AlterRowHandlingDTO{" +
                "userId='" + super.getUserId() + '\'' +
                ", dataModelName='" + super.getDataModelName() + '\'' +
                ", tableName='" + super.getTableName() + '\'' +
                ", saveAs='" + super.getSaveAs() + '\'' +
                ", processor='" + super.getProcessor() + '\'' +
                ", condition=" + super.getCondition().toString() +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

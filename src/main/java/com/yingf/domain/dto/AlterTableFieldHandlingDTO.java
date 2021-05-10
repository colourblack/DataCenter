package com.yingf.domain.dto;

import com.yingf.domain.query.clean.AlterTableFieldHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 10:02 AM
 */
@ApiModel(value = "AlterTableFieldHandlingDTO", description = "数据表字段重命名以及重新排序 - DTO")
public class AlterTableFieldHandlingDTO extends AlterTableFieldHandlingQuery {

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
                ", order=" + super.getOrder().toString() +
                ", rename=" + super.getRename().toString() +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

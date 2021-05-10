package com.yingf.domain.dto;

import com.yingf.domain.query.clean.AddColHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @Description 数据中心 - 清洗分区
 *              批量修改行DTO, 用于向python端发送清洗任务信息
 * @since 5/8/21 3:44 PM
 */
@ApiModel(value = "AddColHandlingDTO", description = "批量新增字段 - DTO")
public class AddColHandlingDTO extends AddColHandlingQuery {

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
                ", colData=" + super.getColData().toString() +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

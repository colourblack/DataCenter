package com.yingf.domain.dto;

import com.yingf.domain.query.clean.DuplicateHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author yingf Fangjunjin
 * @Description 数据中心 - 清洗分区
 *              重复值处理DTO, 用于向python端发送清洗任务信息
 * @Date 2021/3/16
 */
@ApiModel(value = "DuplicateProcessorDTO", description = "重复值处理 - DTO")
public class DuplicateHandlingDTO extends DuplicateHandlingQuery {

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
                ", colName=" + super.getColName() +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

package com.yingf.domain.dto;

import com.yingf.domain.query.clean.NullValueHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 *
 * @author yingf Fangjunjin
 * @Description 数据中心 - 清洗分区
 *              空值处理DTO, 用于向python端发送清洗任务信息
 * @Date 2021/3/16
 */
@ApiModel(value = "NullValueHandlingDTO", description = "空值处理 - DTO")
public class NullValueHandlingDTO  extends NullValueHandlingQuery {

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
        return "NullValueHandlingDTO{" +
                "userId='" + super.getUserId() + '\'' +
                ", dataModelName='" + super.getDataModelName() + '\'' +
                ", tableName='" + super.getTableName() + '\'' +
                ", saveAs='" + super.getSaveAs() + '\'' +
                ", processor='" + super.getProcessor() + '\'' +
                ", nullProcessType='" + super.getNullProcessType() + '\'' +
                ", fillValue='" + super.getFillValue() + '\'' +
                ", col=" + super.getCol().toString() +
                ", row=" + super.getRow().toString() +
                ", handleType=" + handleType +
                '}';
    }
}

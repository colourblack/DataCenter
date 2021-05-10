package com.yingf.domain.dto;

import com.yingf.domain.query.clean.AlterColHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @Description 数据中心 - 清洗分区
 *              字段修改DTO, 用于向python端发送清洗任务信息
 * @since 5/8/21 3:44 PM
 */
@ApiModel(value = "AlterColHandlingDTO", description = "字段修改 - DTO")
public class AlterColHandlingDTO extends AlterColHandlingQuery {

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
                ", colArgs=" + super.getColArgs().toString() +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

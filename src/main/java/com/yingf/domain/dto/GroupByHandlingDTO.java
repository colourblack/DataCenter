package com.yingf.domain.dto;

import com.yingf.domain.query.clean.GroupByHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @Description 数据中心 - 清洗分区
 *              分组聚合运算, 用于向python端发送清洗任务信息
 * @since 5/8/21 3:44 PM
 */
@ApiModel(value = "GroupByHandlingDTO", description = "分组聚合运算 - DTO")
public class GroupByHandlingDTO extends GroupByHandlingQuery {
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
                ", cols=" + super.getCols().toString() +
                ", opr='" + super.getOpr() + '\'' +
                ", axis=" + super.getAxis() +
                ", newName='" + super.getNewName() + '\'' +
                "handleType='" + handleType + '\'' +
                '}';
    }
}

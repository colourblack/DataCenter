package com.yingf.domain.dto;

import com.yingf.domain.query.clean.MergeTableHandlingQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @Description 数据中心 - 清洗分区
 *              多表关联, 用于向python端发送清洗任务信息
 * @since 5/8/21 3:44 PM
 */
@ApiModel(value = "MergeTableHandlingDTO", description = "多表关联 - DTO")
public class MergeTableHandlingDTO  extends MergeTableHandlingQuery {
    @ApiModelProperty("请求python的方法")
    private String handleType;

    private String associationTable;

    public String getAssociationTable() {
        return associationTable;
    }

    public void setAssociationTable(String associationTable) {
        this.associationTable = associationTable;
    }

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
                ", leftOn=" + super.getLeftOn().toString() +
                ", rightOn=" + super.getRightOn().toString() +
                ", how='" + super.getHow() + '\'' +
                ", connType='" + super.getConnType() + '\'' +
                ", maintainCol=" + super.getMaintainCol().toString() +
                ", reviseName=" + super.getReviseName().toString() +
                ", associationTable=" + associationTable +
                "handleType='" + handleType + '\'' +
                '}';
    }

}

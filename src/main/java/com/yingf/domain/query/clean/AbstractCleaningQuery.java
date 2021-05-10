package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author yingf Fangjunjin
 * @Date 2021/3/16
 */
@ApiModel(value = "AbstractCleaningQuery", description = "执行过滤方案参数")
public abstract class AbstractCleaningQuery implements Serializable, CleaningQuery {

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("操作角色id")
    private long userId;

    @ApiModelProperty("数据源名称")
    private String dataModelName;

    @ApiModelProperty("过滤表表名")
    private String tableName;

    @ApiModelProperty("过滤操作另存为名称")
    private String saveAs;

    @Override
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    @Override
    public String toString() {
        return "CleaningQuery{" +
                "userId='" + userId + '\'' +
                ", dataModelName='" + dataModelName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", saveAs='" + saveAs + '\'' +
                '}';
    }
}

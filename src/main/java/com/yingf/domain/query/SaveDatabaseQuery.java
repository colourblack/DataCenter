package com.yingf.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author yingf Fangjunjin
 */
@ApiModel(value = "SaveDatabaseQuery", description = "Database保存数据的参数")
public class SaveDatabaseQuery implements Serializable {

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("数据源名称")
    private String dataModelName;

    @ApiModelProperty("选中的数据表集合")
    private List<String> tableNameList;

    public SaveDatabaseQuery(String datasourceAlias, List<String> tableNameList) {
        this.dataModelName = datasourceAlias;
        this.tableNameList = tableNameList;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public List<String> getTableNameList() {
        return tableNameList;
    }

    public void setTableNameList(List<String> tableNameList) {
        this.tableNameList = tableNameList;
    }

    @Override
    public String toString() {
        return "SaveDatasourceArgs{" +
                ", datasourceAlias='" + dataModelName + '\'' +
                ", tableName=" + tableNameList +
                '}';
    }
}

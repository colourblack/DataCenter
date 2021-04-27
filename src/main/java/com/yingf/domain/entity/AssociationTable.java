package com.yingf.domain.entity;

import java.io.Serializable;

/**
 * @author yingf fangjunjin
 */
public class AssociationTable implements Serializable {

    private final static long serialVersionUID = 1L;

    private String datasourceAlias;
    private String tableName;
    private String tableType;
    private String fieldName;
    private String fieldType;

    public AssociationTable() {

    }

    public String getDatasourceAlias() {
        return datasourceAlias;
    }

    public void setDatasourceAlias(String datasourceAlias) {
        this.datasourceAlias = datasourceAlias;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}

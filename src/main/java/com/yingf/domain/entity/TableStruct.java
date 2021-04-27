package com.yingf.domain.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yingf fangjunjin
 */
public class TableStruct implements Serializable {

    private static final long serialVersionUID = 123L;

    private String datasourceAlias;
    private String tableName;
    private String tableType;
    private int count;
    private List<String> fieldName;
    private Map<String, String> fieldType;
    private Map<String, String> groupBy;
    private List<AssociationTable> associationTable;


    public TableStruct() {

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getFieldName() {
        return fieldName;
    }

    public void setFieldName(List<String> fieldName) {
        this.fieldName = fieldName;
    }

    public Map<String, String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Map<String, String> groupBy) {
        this.groupBy = groupBy;
    }

    public List<AssociationTable> getAssociationTable() {
        return associationTable;
    }

    public void setAssociationTable(List<AssociationTable> associationTable) {
        this.associationTable = associationTable;
    }

    public Map<String, String> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Map<String, String> fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return "TableStruct{" +
                "datasourceAlias='" + datasourceAlias + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                ", count=" + count +
                ", fieldName=" + fieldName +
                ", fieldType=" + fieldType +
                ", groupBy=" + groupBy +
                ", associationTable=" + associationTable +
                '}';
    }
}

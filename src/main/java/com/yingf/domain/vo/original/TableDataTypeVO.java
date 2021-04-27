package com.yingf.domain.vo.original;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 数据表的数据类型
 * @Date 2021/3/12
 */
public class TableDataTypeVO implements Serializable {

    private List<String> fieldName;

    private Map<String, String> fieldType;

    public List<String> getFieldName() {
        return fieldName;
    }

    public void setFieldName(List<String> fieldName) {
        this.fieldName = fieldName;
    }

    public Map<String, String> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Map<String, String> fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return "TableDataTypeVO{" +
                "fieldName=" + fieldName +
                ", fieldType=" + fieldType +
                '}';
    }
}

package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 4:37 PM
 */
@ApiModel(value = "AddColQuery", description = "新增字段的参数")
public class AddColHandlingQuery extends AbstractCleaningQuery{
    private final static long serialVersionUID = 1L;

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("新增字段的参数")
    private List<AddColHandlingQuery.ColStruct> colData;

    public AddColHandlingQuery() {

    }

    public AddColHandlingQuery(String processor, List<AddColHandlingQuery.ColStruct> colData) {
        this.processor = processor;
        this.colData = colData;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<AddColHandlingQuery.ColStruct> getColData() {
        return colData;
    }

    public void setColData(List<AddColHandlingQuery.ColStruct> colData) {
        this.colData = colData;
    }

    @ApiModel(value = "ColStruct", description = "新增字段参数")
    public static class ColStruct {

        @ApiModelProperty("字段名")
        private String colName;
        @ApiModelProperty("字段默认值")
        private String defaultVal;
        @ApiModelProperty("字段类型")
        private String colType;


        public ColStruct( String colName, String defaultVal, String colType) {
            this.colName = colName;
            this.defaultVal = defaultVal;
            this.colType = colType;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getDefaultVal() {
            return defaultVal;
        }

        public void setDefaultVal(String defaultVal) {
            this.defaultVal = defaultVal;
        }

        public String getColType() {
            return colType;
        }

        public void setColType(String colType) {
            this.colType = colType;
        }

        @Override
        public String toString() {
            return "字段名='" + colName + '\'' + ", 字段类型='" + colType + '\'' + ", 字段默认值='" + defaultVal + '\'';
        }
    }


    @Override
    public String toString() {
        return "AddColHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", colData=" + colData +
                '}';
    }
}

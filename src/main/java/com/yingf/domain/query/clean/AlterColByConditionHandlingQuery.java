package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 5:30 PM
 */
@ApiModel(value = "AlterColByConditionHandlingQuery", description = "根据条件修改指定字段的值")
public class AlterColByConditionHandlingQuery extends AbstractCleaningQuery{

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("条件参数")
    private List<AlterColByConditionHandlingQuery.ConditionArgs> condition;

    @ApiModelProperty("修改值")
    private AddColHandlingQuery.ColStruct colData;

    public AlterColByConditionHandlingQuery() {}


    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<AlterColByConditionHandlingQuery.ConditionArgs> getCondition() {
        return condition;
    }

    public void setCondition(List<AlterColByConditionHandlingQuery.ConditionArgs> condition) {
        this.condition = condition;
    }

    public AddColHandlingQuery.ColStruct getColData() {
        return colData;
    }

    public void setColData(AddColHandlingQuery.ColStruct colData) {
        this.colData = colData;
    }

    public static class ConditionArgs{
        @ApiModelProperty("字段名")
        private String colName;

        @ApiModelProperty("字段类型")
        private String colType;

        @ApiModelProperty("条件比较运算符")
        private String operator;

        @ApiModelProperty("条件值")
        private Object colVal;

        @ApiModelProperty("条件关系运算符")
        private String logic;

        public ConditionArgs(String colName, String colType, String operator, Object colVal, String logic) {
            this.colName = colName;
            this.colType = colType;
            this.operator = operator;
            this.colVal = colVal;
            this.logic = logic;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getColType() {
            return colType;
        }

        public void setColType(String colType) {
            this.colType = colType;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Object getColVal() {
            return colVal;
        }

        public void setColVal(Object colVal) {
            this.colVal = colVal;
        }

        public String getLogic() {
            return logic;
        }

        public void setLogic(String logic) {
            this.logic = logic;
        }

        @Override
        public String toString() {
            return "ConditionArgs{" +
                    "colName='" + colName + '\'' +
                    ", colType='" + colType + '\'' +
                    ", operator='" + operator + '\'' +
                    ", colVal=" + colVal +
                    ", logic='" + logic + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AlterColByConditionHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", condition=" + condition.toString() +
                ", colData=" + colData.toString() +
                '}';
    }
}

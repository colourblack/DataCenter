package com.yingf.domain.query.clean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 5:04 PM
 */
@ApiModel(value = "AlterColHandlingQuery", description = "数据校验")
public class AlterColHandlingQuery extends AbstractCleaningQuery {

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("校验参数")
    private AlterColHandlingQuery.CalibrationParam colArgs;

    public AlterColHandlingQuery() {

    }

    public AlterColHandlingQuery(String processor, AlterColHandlingQuery.CalibrationParam colArgs) {
        this.processor = processor;
        this.colArgs = colArgs;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public AlterColHandlingQuery.CalibrationParam getColArgs() {
        return colArgs;
    }

    public void setColArgs(AlterColHandlingQuery.CalibrationParam colArgs) {
        this.colArgs = colArgs;
    }

    @ApiModel(value = "CalibrationParam", description = "数据校验参数")
    public static class CalibrationParam {

        @ApiModelProperty("字段名")
        private String colName;

        @ApiModelProperty("字段类型")
        private String colType;

        @ApiModelProperty("填充值格式")
        private String contentType;

        @ApiModelProperty("填充值")
        private String fillVal;

        @ApiModelProperty("非法值处理方式")
        private String invalidValHandler;

        @ApiModelProperty("重命名名称")
        private String rename;

        public CalibrationParam() {

        }

        public CalibrationParam(String colName, String colType, String contentType, String fillVal, String invalidValHandler, String rename) {
            this.colName = colName;
            this.colType = colType;
            this.contentType = contentType;
            this.fillVal = fillVal;
            this.invalidValHandler = invalidValHandler;
            this.rename = rename;
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

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFillVal() {
            return fillVal;
        }

        public void setFillVal(String fillVal) {
            this.fillVal = fillVal;
        }

        public String getInvalidValHandler() {
            return invalidValHandler;
        }

        public void setInvalidValHandler(String invalidValHandler) {
            this.invalidValHandler = invalidValHandler;
        }

        public String getRename() {
            return rename;
        }

        public void setRename(String rename) {
            this.rename = rename;
        }

        @Override
        public String toString() {
            return "CalibrationParam{" +
                    "colName='" + colName + '\'' +
                    ", colType='" + colType + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", fillVal='" + fillVal + '\'' +
                    ", invalidValHandler='" + invalidValHandler + '\'' +
                    ", rename='" + rename + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AlterColHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", colArgs=" + colArgs.toString() +
                '}';
    }
}

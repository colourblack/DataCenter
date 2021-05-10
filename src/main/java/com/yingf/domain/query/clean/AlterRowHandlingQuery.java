package com.yingf.domain.query.clean;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 3:39 PM
 */
@ApiModel(value = "AlterRowProcessorQuery", description = "记录修改的参数")
public class AlterRowHandlingQuery extends AbstractCleaningQuery {

    @ApiModelProperty("处理方案")
    private String processor;

    @ApiModelProperty("记录号")
    private List<Integer> row;

    @ApiModelProperty("批量新增记录数据")
    private List<JSONObject> rowBatch;

    @ApiModelProperty("指定记录的数据")
    private JSONObject rowData;

    public AlterRowHandlingQuery() {

    }

    public AlterRowHandlingQuery(String processor, List<Integer> row, List<JSONObject> rowBatch, JSONObject rowData) {
        this.processor = processor;
        this.row = row;
        this.rowBatch = rowBatch;
        this.rowData = rowData;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<Integer> getRow() {
        return row;
    }

    public void setRow(List<Integer> row) {
        this.row = row;
    }

    public List<JSONObject> getRowBatch() {
        return rowBatch;
    }

    public void setRowBatch(List<JSONObject> rowBatch) {
        this.rowBatch = rowBatch;
    }

    public JSONObject getRowData() {
        return rowData;
    }

    public void setRowData(JSONObject rowData) {
        this.rowData = rowData;
    }

    @Override
    public String toString() {
        return "AlterRowHandlingQuery{" +
                "processor='" + processor + '\'' +
                ", row=" + row +
                ", rowBatch=" + rowBatch +
                ", rowData=" + rowData +
                '}';
    }
}

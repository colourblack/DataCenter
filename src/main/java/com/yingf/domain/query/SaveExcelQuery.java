package com.yingf.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author yingf Fangjunjin
 * @Description 第三方数据源为Excel时, 用于将Excel中选中的Sheet数据保存至mongo db
 * @Date 2021/3/11
 */
@ApiModel(value = "SaveExcelQuery", description = "Excel保存数据的参数")
public class SaveExcelQuery implements Serializable {

    private final static long serialVersionUID = 1L;

    @ApiModelProperty("数据源名称")
    private String dataModelName;

    @ApiModelProperty("选中的sheet集合")
    private List<String> sheetNameList;

    @ApiModelProperty("文件的Md5值")
    private String fileMd5;

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public List<String> getSheetNameList() {
        return sheetNameList;
    }

    public void setSheetNameList(List<String> sheetNameList) {
        this.sheetNameList = sheetNameList;
    }

    public String getFileMd5() {

        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }
}

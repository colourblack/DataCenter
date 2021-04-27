package com.yingf.domain.vo.original;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 数据中心 - 创建数据源
 *              当第三方数据源为Excel文件时, 用该实体类保存预览信息
 * @Date 2021/3/10
 */
public class ExcelPreviewInfoVO implements Serializable {

    /**
     * 存储Excel中sheet name的
     * key:   index
     * value: sheet name
     */
    private Map<Integer, String> sheetNameMap;

    /**
     * key:   Excel中每一个sheet的名称
     * value: sheet中所有的字段名(使用List存储)
     */
    private Map<String, List<String>> header;

    /**
     * key:   Excel中每一个sheet的名称
     * value: sheet中所有成功解析记录(All records)
     */
    private Map<String, List<Map<String, Object>>> records;

    /**
     * key:   Excel中每一个sheet的名称
     * value: sheet中所有解析失败的记录行号
     */
    private Map<String, List<Integer>> errorRecords;

    /**
     * key:   Excel中每一个sheet的名称
     * value: sheet中总记录数目
     */
    private Map<String, Integer> totalCount;

    public Map<Integer, String> getSheetNameMap() {
        return sheetNameMap;
    }

    public void setSheetNameMap(Map<Integer, String> sheetNameMap) {
        this.sheetNameMap = sheetNameMap;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public Map<String, List<Map<String, Object>>> getRecords() {
        return records;
    }

    public void setRecords(Map<String, List<Map<String, Object>>> records) {
        this.records = records;
    }

    public Map<String, List<Integer>> getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(Map<String, List<Integer>> errorRecords) {
        this.errorRecords = errorRecords;
    }

    public Map<String, Integer> getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Map<String, Integer> totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "ExcelPreviewInfoVO{" +
                "sheetList=" + sheetNameMap +
                ", header=" + header +
                ", records=" + records +
                ", errorRecords=" + errorRecords +
                ", totalCount=" + totalCount +
                '}';
    }
}

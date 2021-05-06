package com.yingf.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 第一次解析, 用户获取Excel预览信息
 * @Date 2021/3/10
 */
public class PreviewInfoListener extends AnalysisEventListener <Map<Integer, Object>> {

    private static final Logger log = LoggerFactory.getLogger(PreviewInfoListener.class);

    /**
     * 预览内容只存储20条
     */
    private final int previewCount = 20;

    /**
     * 本次解析的过程中发生错误的数据记录行号
     */
    private final List<Integer> errorRecords = new ArrayList<>();

    /**
     * 预览内容的前20条数据明细
     */
    private final List<Map<String, Object>> records = new ArrayList<>(previewCount);

    /**
     *  该sheet的表头
     */
    private Map<Integer, String> header = new HashMap<>();

    /**
     * 该sheet的总数据
     */
    private int totalCount = 0;

    /**
     * 这里会一行行的返回头
     *
     * @param headMap 头数据map
     * @param context Easy excel 解析上下文
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.debug("解析到一条头数据:{}", JSON.toJSONString(headMap));
        this.header = headMap;
    }


    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        if (totalCount < previewCount) {
            Map<String, Object> record = new HashMap<>(2);
            data.forEach((key, value) -> record.put(header.get(key), value));
            records.add(record);
        }
        totalCount = totalCount + 1;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("所有数据解析完成, 一共{}数据", totalCount);
    }

    /**
     *
     * @param exception 异常
     * @param context   Easy Excel的解析上下文
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
        errorRecords.add(totalCount);
    }

    public List<Integer> getErrorRecords() {
        return errorRecords;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public List<String> getHeaderList() {
        List<String> headerList = new ArrayList<>();
        header.forEach((key, value) -> headerList.add(value));
        return headerList;
    }

    public int getTotalCount() {
        return totalCount;
    }
}

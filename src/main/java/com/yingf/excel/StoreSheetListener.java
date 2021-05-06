package com.yingf.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 用于将Sheet存入mongo db
 * @Date 2021/3/11
 */
public class StoreSheetListener extends AnalysisEventListener<Map<Integer, Object>> {

    private static final Logger log = LoggerFactory.getLogger(StoreSheetListener.class);


    /**
     *  该sheet的表头
     */
    private Map<Integer, String> header = new HashMap<>();

    private static final int BATCH_COUNT = 3000;

    private int totalCount = 0;

    private final String collectionName;

    private final List<Map<String, Object>> records;

    private final MongoTemplate mongoDbOriginalTemplate;

    private final MongoTemplate mongoDbFilterTemplate;

    private boolean isSucceed = false;


    public StoreSheetListener(MongoTemplate mongoDbOriginalTemplate, MongoTemplate mongoDbFilterTemplate,
                     String collectionName) {
        this.mongoDbOriginalTemplate = mongoDbOriginalTemplate;
        this.mongoDbFilterTemplate = mongoDbFilterTemplate;
        // 获取mongo db collection name
        this.collectionName = collectionName;
        this.records = new ArrayList<>(BATCH_COUNT);
        log.debug("Mongodb的集合(Collection)名称:{}", collectionName);
    }



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
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
        }
    }


    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext analysisContext) {
        Map<String, Object> record = new HashMap<>(2);
        data.forEach((key, value) -> record.put(header.get(key), value));
        records.add(record);
        if (records.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            records.clear();
        }

        totalCount++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 最后也需要插入数据, list最后的数据顺利入库
        saveData();
        records.clear();
        log.debug("所有数据解析完成, 一共{}数据", totalCount);
        isSucceed = true;
    }

    /**
     * 将records中的数据存入mongo db, 避免过多数据在JVM中滞留
     */
    private void saveData() {
        mongoDbOriginalTemplate.insert(records, collectionName);
        mongoDbFilterTemplate.insert(records, collectionName);
    }

    public boolean isSuccess() {
        if (!isSucceed) {
            log.warn("Excel文件解析失败");
        }
        return isSucceed;
    }
}

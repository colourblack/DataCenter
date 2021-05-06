package com.yingf.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.analysis.ExcelReadExecutor;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.vo.original.ExcelPreviewInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description Excel 文件解析工具
 * @Date 2021/3/10
 */
@Component
public class ExcelParseProcessor {

    private static final Logger log = LoggerFactory.getLogger(ExcelParseProcessor.class);

    final MongoTemplate mongoDbOriginalTemplate;

    final MongoTemplate mongoDbFilterTemplate;

    public ExcelParseProcessor(MongoTemplate mongoDbOriginalTemplate, MongoTemplate mongoDbFilterTemplate) {
        this.mongoDbOriginalTemplate = mongoDbOriginalTemplate;
        this.mongoDbFilterTemplate = mongoDbFilterTemplate;
    }


    /**
     * 解析Excel文件, 获取相应的预览信息
     * @param filePath 文件路径
     * @return 预览信息 or Null
     */
    public ExcelPreviewInfoVO doPreviewProcess(String filePath) {
        ExcelPreviewInfoVO excelPreviewInfoVO = new ExcelPreviewInfoVO();
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(filePath).build();
            ExcelReadExecutor excelReadExecutor = excelReader.excelExecutor();
            List<ReadSheet> readSheets = excelReadExecutor.sheetList();
            int size = readSheets.size();

            /* 存储Excel中sheet name的List */
            Map<Integer, String> sheetNameMap = new HashMap<>(size);

            /* key:   Excel中每一个sheet的名称, value: sheet中所有的字段名(使用List存储) */
            Map<String, List<String>> header = new HashMap<>(size);

            /* key:   Excel中每一个sheet的名称, value: sheet中所有成功解析记录(All records) */
            Map<String, List<Map<String, Object>>> records = new HashMap<>(size);

            /* key:   Excel中每一个sheet的名称, value: sheet中所有解析失败的记录行号 */
            Map<String, List<Integer>> errorRecords = new HashMap<>(size);

            /* key:   Excel中每一个sheet的名称, value: sheet中所有的字段名(使用List存储) */
            Map<String, Integer> totalCount = new HashMap<>(size);

            for (int i = 0; i < readSheets.size(); i++) {
                // 将EXCEL文件中的sheet name添加sheetList中
                String sheetName = readSheets.get(i).getSheetName();

                // 初始化EasyExcel用于解析excel文件的监听器
                PreviewInfoListener previewInfoListener = new PreviewInfoListener();
                ReadSheet readSheet = EasyExcel.readSheet(i).registerReadListener(previewInfoListener).build();
                excelReader.read(readSheet);

                // 将当前sheet解析结果存入ExcelPreviewInfo对象
                sheetNameMap.put(i, sheetName);
                header.put(sheetName, previewInfoListener.getHeaderList());
                records.put(sheetName, previewInfoListener.getRecords());
                errorRecords.put(sheetName, previewInfoListener.getErrorRecords());
                totalCount.put(sheetName, previewInfoListener.getTotalCount());
            }

            // 解析完成以后将结果放入Excel预览对象
            excelPreviewInfoVO.setSheetNameMap(sheetNameMap);
            excelPreviewInfoVO.setHeader(header);
            excelPreviewInfoVO.setRecords(records);
            excelPreviewInfoVO.setErrorRecords(errorRecords);
            excelPreviewInfoVO.setTotalCount(totalCount);
        } catch (Exception e) {
            log.error("无法正确解析文件, 该文件路径为:{}", filePath);
            log.error(e.getMessage());
            excelPreviewInfoVO = null;
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
        return excelPreviewInfoVO;
    }


    public boolean doStoreProcess(String dataModelName, String filePath, List<String> sheetNameList) {
        ExcelReader excelReader = null;
        // 标记位, 用于记录当前sheetNameList的下标
        int index = 0;
        log.debug("解析存储的Excel路径为:{}", filePath);
        try {
            excelReader = EasyExcel.read(filePath).build();
            ExcelReadExecutor excelReadExecutor = excelReader.excelExecutor();
            List<ReadSheet> readSheets = excelReadExecutor.sheetList();
            for (int i = 0; i < readSheets.size(); i++) {
                // 将EXCEL文件中的sheet name添加sheetList中
                String sheetName = readSheets.get(i).getSheetName();
                if (sheetNameList.contains(sheetName)) {
                    index ++;
                    // 获取mongo db collection name
                    String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + sheetName;

                    // 初始化EasyExcel用于解析excel文件的监听器
                    StoreSheetListener storeSheetListener = new StoreSheetListener(mongoDbOriginalTemplate,
                            mongoDbFilterTemplate, collectionName);

                    ReadSheet readSheet = EasyExcel.readSheet(i).registerReadListener(storeSheetListener).build();
                    excelReader.read(readSheet);

                    // 检查当前Excel文件解析是否正确
                    if (!storeSheetListener.isSuccess()) {
                        break;
                    }
                }

            }
        } catch (Exception e) {
            log.error("无法正确解析文件, 该文件路径为:{}", filePath);
            log.error(e.getMessage());
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
        if (index != sheetNameList.size()) {
            for (int i = 0; i < index; i++) {
                // 获取mongo db collection name
                String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + sheetNameList.get(i);
                mongoDbOriginalTemplate.dropCollection(collectionName);
                mongoDbFilterTemplate.dropCollection(collectionName);
            }
            return false;
        }
        return true;
    }
}

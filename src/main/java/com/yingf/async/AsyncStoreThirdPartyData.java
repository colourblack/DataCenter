package com.yingf.async;

import com.yingf.constant.DataCenterConstant;
import com.yingf.database.IDataBaseWorkerService;
import com.yingf.domain.entity.DataWareHouseDatabaseInfo;
import com.yingf.domain.entity.DataWareHouseFileInfo;
import com.yingf.domain.vo.original.GenerateTableStructVO;
import com.yingf.excel.ExcelParseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 异步存储table到Csv文件中
 *
 * @author yinf Fangjunjin
 */
@Component
public class AsyncStoreThirdPartyData {

    Logger log = LoggerFactory.getLogger(AsyncStoreThirdPartyData.class);

    final IDataBaseWorkerService dataBaseWorkerServiceImpl;

    final ExcelParseProcessor excelParseProcessor;

    final KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    public AsyncStoreThirdPartyData(IDataBaseWorkerService dataBaseWorkerServiceImpl,
                                    ExcelParseProcessor excelParseProcessor, KafkaTemplate<String, Object> kafkaTemplate) {
        this.dataBaseWorkerServiceImpl = dataBaseWorkerServiceImpl;
        this.excelParseProcessor = excelParseProcessor;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 将选中的数据表存储到mongodb中
     * @param databaseInfo  第三方数据库信息信息
     * @param tableNameList 选中的所有列表名
     * @param userId        用户id
     * @return 存储结果
     */
    @Async
    public Future<Boolean> asyncStoreDatabase(DataWareHouseDatabaseInfo databaseInfo, List<String> tableNameList, long userId) {
        if (dataBaseWorkerServiceImpl.storeTableToMongoDb(databaseInfo, tableNameList, databaseInfo.getDataModelName())) {
            log.debug("异步存储已经完成! 通过kafka通知python对数据表进行数据类型分析");
            sendMsgToGenerateTableStruct(databaseInfo.getDataModelName(), tableNameList, userId);
            return new AsyncResult<>(true);
        } else {
            log.warn("数据库异步存储发生错误！当前的数据模型为:{}", databaseInfo.getDataModelName());
            return new AsyncResult<>(false);
        }
    }

    /**
     * 将选中的数据表存储到mongodb中
     * @param dataWareHouseFileInfo  第三方Excel文件信息
     * @param sheetNameList          选中的所有列表名
     * @param userId                 用户id
     * @return 存储结果
     */
    @Async
    public Future<Boolean> asyncStoreExcel(DataWareHouseFileInfo dataWareHouseFileInfo, List<String> sheetNameList, long userId) {
        if (excelParseProcessor.doStoreProcess(dataWareHouseFileInfo.getDataModelName(), dataWareHouseFileInfo.getFilePath(),
                sheetNameList)) {
            log.debug("异步存储已经完成! 通过kafka通知python对数据表进行数据类型分析");
            sendMsgToGenerateTableStruct(dataWareHouseFileInfo.getDataModelName(), sheetNameList, userId);
            return new AsyncResult<>(true);
        } else {
            log.warn("数据库异步存储发生错误！当前的数据模型为:{}", dataWareHouseFileInfo.getDataModelName());
            return new AsyncResult<>(false);
        }
    }

    private void sendMsgToGenerateTableStruct(String dataModelName, List<String> tableNameList, Long userId) {
        GenerateTableStructVO generateTableStructVO = new GenerateTableStructVO();
        generateTableStructVO.setDataModelName(dataModelName);
        generateTableStructVO.setTableNameList(tableNameList);
        generateTableStructVO.setUserId(userId);
        generateTableStructVO.setHandleType(DataCenterConstant.TABLE_STRUCT_REQ);
        kafkaTemplate.send(DataCenterConstant.TABLE_STRUCT_TOPIC, generateTableStructVO);
    }

}

package com.yingf.service.impl;

import com.yingf.domain.entity.AssociationTable;
import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.vo.original.TableDataTypeVO;
import com.yingf.service.IDataCenterCleaningService;
import com.yingf.service.IDataCenterCommonService;
import com.yingf.util.DataModelRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author yingf Fangjunjin
 * @Description 负责数据清洗分区的Service
 * @Date 2021/3/16
 */
@Service
public class DataCenterCleaningServiceImpl implements IDataCenterCleaningService {

    private static final Logger log = LoggerFactory.getLogger(DataCenterCleaningServiceImpl.class);

    final DataModelRedisUtil dataModelRedisUtil;

    final IDataCenterCommonService dataCenterCommonServiceImpl;

    final KafkaTemplate<String, Object> kafkaTemplate;

    public DataCenterCleaningServiceImpl(DataModelRedisUtil dataModelRedisUtil, IDataCenterCommonService dataCenterCommonServiceImpl,
                                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.dataModelRedisUtil = dataModelRedisUtil;
        this.dataCenterCommonServiceImpl = dataCenterCommonServiceImpl;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public TableDataTypeVO getTableDataTypeFromRedis(String dataModelName, String tableName, String saveAs) {
        TableDataTypeVO tableDataTypeVO = new TableDataTypeVO();
        TableStruct tableStruct = getSaveAsTableStruct(dataModelName, saveAs, tableName);
        if (tableStruct == null) {
            log.warn("查看[{}]分区的[{}]表表结构信息(另存为方案为:{})", dataModelName, tableName, saveAs);
            return null;
        }
        /* 获取原表字段名和字段类型，同时需要将子表的字段名和字段类型一起加上返回 */
        List<String> fieldNames = tableStruct.getFieldName();
        Map<String, String> fieldTypes = tableStruct.getFieldType();
        List<AssociationTable> associationTables;
        associationTables = tableStruct.getAssociationTable();
        if (associationTables.size() != 0) {
            for (AssociationTable associationTable : associationTables) {
                fieldNames.add(associationTable.getFieldName());
                fieldTypes.put(associationTable.getFieldName(), associationTable.getFieldType());
            }
        }
        tableDataTypeVO.setFieldName(fieldNames);
        tableDataTypeVO.setFieldType(fieldTypes);
        return tableDataTypeVO;
    }


    @Override
    public TableStruct getSaveAsTableStruct(String dataModelName, String tableName, String saveAs) {
        String redisKey = DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + dataModelName
                + DataCenterConstant.FILTER_PARTITION + saveAs;
        TableStruct tableStruct;
        if (dataModelRedisUtil.existsKey(redisKey)) {
            tableStruct = (TableStruct) dataModelRedisUtil.getValue(redisKey);
        } else {
            log.debug("查看[{}]分区的[{}]表表结构信息", dataModelName, tableName);
            tableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(dataModelName, tableName, DataCenterConstant.FILTER_PARTITION);
            dataModelRedisUtil.setValue(redisKey, tableStruct);
        }
        return tableStruct;
    }


    @Override
    public boolean checkAvailableTask(CleaningQuery query) {
        // 标志位： 标志此次操作是否合法
        boolean valid = false;
        // redis: 表示数据表状态的key
        String tableStatusRedisKey = DataCenterConstant.REDIS_TABLE_STATUS_PREFIX + DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getTableName();

        try {
            /* 操作的表对象已经存在且此次操作为同一个用户执行 */
            if (query.getUserId() == ((Long) dataModelRedisUtil.getValue(tableStatusRedisKey))) {
                valid = true;
            } else {
                // 关于所有另存为表的redis key
                String saveAsKeySuffix = DataCenterConstant.FILTER_PARTITION
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getSaveAs();
                // redis: 表示数据表关联子表的key(这个值用list存储)
                String associationTableListKey = DataCenterConstant.REDIS_ASSOCIATION_TABLE_LIST_PREFIX + saveAsKeySuffix;
                // redis: 表示数据表表结构信息的key(此时的key表示的是另存为表的key)
                String saveAsStructRedisKey = DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + saveAsKeySuffix;
                // 获取redis list：AssociationTableHash所有的value
                List<Object> associationList = dataModelRedisUtil.listGet(associationTableListKey, 0, -1);

                if (!query.getSaveAs().equals(query.getTableName())) {
                    /* 若操作表不是原表上进行操作 */
                    /* redis: 确保要修改的表的状态改变为status */
                    dataModelRedisUtil.setValue(tableStatusRedisKey, query.getUserId());
                    /* redis: TABLE_STRUCT_INFO 建立新的表结构信息，确保最后任务执行完成后进行存储*/
                    if (!dataModelRedisUtil.existsKey(saveAsStructRedisKey)) {
                        TableStruct tableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(query.getDataModelName(),
                                query.getTableName(), DataCenterConstant.FILTER_PARTITION);
                        dataModelRedisUtil.setValue(saveAsStructRedisKey, tableStruct);
                    }
                    valid = true;
                } else {
                    /* 若操作表是在原表上进行操作，则需要判断该表是否在associationTableHash的关联子表中 */
                    if (!objectListHasVal(associationList, saveAsKeySuffix)) {
                        valid = true;
                        /* redis: 确保要修改的表的状态改变为status */
                        dataModelRedisUtil.setValue(tableStatusRedisKey, query.getUserId());

                        // redis: 表示数据表表结构信息的key(此时的key表示的是另存为表的key)
                        String tableStructRedisKey = DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + DataCenterConstant.FILTER_PARTITION
                                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getTableName();
                        if (!dataModelRedisUtil.existsKey(tableStructRedisKey)) {
                            TableStruct tableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(query.getDataModelName(),
                                    query.getTableName(), DataCenterConstant.FILTER_PARTITION);
                            dataModelRedisUtil.setValue(tableStructRedisKey, tableStruct);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("数据中心 - 清洗分区: 在检测任务是否合法时出现错误!");
            log.error(e.getMessage());
            valid = false;
        }
        return valid;
    }


    @Override
    public String sendTaskMessage(CleaningQuery query) {
        try {
            kafkaTemplate.send(DataCenterConstant.FILTER_TASK_CHANNEL, query).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            log.error("Kafka向python推送任务失败!");
            return CleaningPlanConstant.FAILED;
        }
        return CleaningPlanConstant.SUCCESS;
    }

    /**
     * 判断redis获取的value List中是否存在指定的值var
     */
    private boolean objectListHasVal(List<Object> objectList, String var) {
        for (Object o : objectList) {
            String str = (String) o;
            if (var.equals(str)) {
                return true;
            }
        }
        return false;
    }
}


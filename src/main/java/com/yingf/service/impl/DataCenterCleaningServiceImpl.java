package com.yingf.service.impl;

import com.yingf.constant.CleaningPlanConstant;
import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.entity.AssociationTable;
import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.query.clean.*;
import com.yingf.domain.vo.original.TableDataTypeVO;
import com.yingf.service.IDataCenterCleaningService;
import com.yingf.service.IDataCenterCommonService;
import com.yingf.util.DataModelRedisUtil;
import com.yingf.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final DataModelRedisUtil dataModelRedisUtil;

    private final IDataCenterCommonService dataCenterCommonServiceImpl;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public DataCenterCleaningServiceImpl(DataModelRedisUtil dataModelRedisUtil, IDataCenterCommonService dataCenterCommonServiceImpl,
                                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.dataModelRedisUtil = dataModelRedisUtil;
        this.dataCenterCommonServiceImpl = dataCenterCommonServiceImpl;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String getLogInfo(String dataModelName, String tableName) {
        // todo 从mongodb中获取清洗日志. 日志在python中生成的
        return null;
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
        /* 标志位： 标志此次操作是否合法 */
        boolean valid;
        /* 指定表格的状态(是否正在使用中) */
        String tableStatusRedisKey;
        /* 指定表格的表数据结构信息 */
        String tableStructRedisKey;
        try {
            if (query.getSaveAs().equals(query.getTableName())) {
                /* 若操作表是在原表上进行操作 */
                String tableKeySuffix = DataCenterConstant.FILTER_PARTITION
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getTableName();
                tableStatusRedisKey = DataCenterConstant.REDIS_TABLE_STATUS_PREFIX + tableKeySuffix;
                tableStructRedisKey = DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + tableKeySuffix;
                /* 操作的表对象已经存在且此次操作为同一个用户执行 */
                /* redis: 表示数据表状态的key : TABLE_STATUS-filter.dataModelName.tableName(saveAsName) */
                if (query.getUserId() == ((Long) dataModelRedisUtil.getValue(tableStatusRedisKey))) {
                    valid = true;
                } else {
                    /* todo 需要判断原表是否在其他表的关联子表中, 否则会可能产生脏数据
                     * 因为此时其他表可能利用此表进行多表关联操作  */
                    valid = true;
                    /* redis: 确保要修改的表的状态改变为status */
                    dataModelRedisUtil.setValue(tableStatusRedisKey, query.getUserId());

                    /* redis: 表示数据表表结构信息的key */
                    if (!dataModelRedisUtil.existsKey(tableStructRedisKey)) {
                        // 若key不存在, 则应当去mongodb中查找
                        TableStruct tableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(query.getDataModelName(),
                                query.getTableName(), DataCenterConstant.FILTER_PARTITION);
                        dataModelRedisUtil.setValue(tableStructRedisKey, tableStruct);
                    }
                }
            } else {
                /* 若操作表是不在原表上进行操作, 而是将操作结果另存为 */
                String saveAsSuffix = DataCenterConstant.FILTER_PARTITION
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                        + DataCenterConstant.MONGO_COLLECTION_SEP + query.getSaveAs();
                tableStatusRedisKey = DataCenterConstant.REDIS_TABLE_STATUS_PREFIX + saveAsSuffix;
                tableStructRedisKey = DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + saveAsSuffix;
                /* 操作的表对象已经存在且此次操作为同一个用户执行 */
                if (query.getUserId() == ((Long) dataModelRedisUtil.getValue(tableStatusRedisKey))) {
                    valid = true;
                } else {
                    valid = true;
                    /* redis: 确保要修改的`另存为表`的状态改变为status */
                    dataModelRedisUtil.setValue(tableStatusRedisKey, query.getUserId());
                    /* redis: 此时`另存为表`的结构信息应该为`原始表`的*/
                    TableStruct tableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(query.getDataModelName(),
                            query.getTableName(), DataCenterConstant.FILTER_PARTITION);
                    dataModelRedisUtil.setValue(tableStructRedisKey, tableStruct);
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


    @Override
    public TableStruct renameField(TableStruct tableStruct, String oldName, String newName) {
        if (oldName.equals(newName)) {
            return tableStruct;
        }
        try {
            List<String> fieldName = tableStruct.getFieldName();
            Map<String, String> fieldType = tableStruct.getFieldType();
            if (fieldName.contains(oldName)) {
                fieldName.add(newName);
                fieldType.put(newName, fieldType.get(oldName));
                fieldName.remove(oldName);
                fieldType.remove(oldName);
                tableStruct.setFieldName(fieldName);
                tableStruct.setFieldType(fieldType);
            } else {
                List<AssociationTable> associationTables = tableStruct.getAssociationTable();
                for (AssociationTable a : associationTables) {
                    if (a.getFieldName().equals(oldName)) {
                        a.setFieldName(newName);
                        tableStruct.setAssociationTable(associationTables);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableStruct;
    }



    @Override
    public boolean modifyTableStructWhenAddCol(AddColHandlingQuery query) {
        TableStruct tableStruct = getSaveAsTableStruct(query.getDataModelName(),query.getSaveAs(), query.getTableName());
        // 将参数转型
        List<AddColHandlingQuery.ColStruct> colStruct = query.getColData();
        List<String> fieldName = tableStruct.getFieldName();
        Map<String, String> fieldType = tableStruct.getFieldType();
        String colName;
        for (AddColHandlingQuery.ColStruct arg : colStruct) {
            colName = arg.getColName();
            fieldName.add(colName);
            fieldType.put(colName, arg.getColType());
        }
        // 更新redis缓存中的数据表结构信息
        dataModelRedisUtil.setValue(DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX+ DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.FILTER_PARTITION + query.getSaveAs(), tableStruct);
        return true;
    }


    @Override
    public boolean modifyTableStructWhenDelCol(DelColHandlingQuery query) {
        // 进行删除操作时, 需要
        String datasourceAlias = query.getDataModelName();
        String saveAs = query.getSaveAs();
        String tableName = query.getTableName();
        List<String> fieldName = query.getColData();
        log.debug(Arrays.toString(fieldName.toArray()));
        TableStruct tableStruct = getSaveAsTableStruct(datasourceAlias, saveAs, tableName);
        List<String> existFieldName = tableStruct.getFieldName();
        Map<String, String> existFieldType = tableStruct.getFieldType();
        List<AssociationTable> existAssociationTable = tableStruct.getAssociationTable();
        int length = fieldName.size();
        log.info("field name size: {}", length);
        for (String filed : fieldName) {
            if (existFieldName.contains(filed)) {
                existFieldName.remove(filed);
                existFieldType.remove(filed);
            } else {
                existAssociationTable.removeIf(childTable -> childTable.getFieldName().equals(filed));
            }
            length--;
        }
        if (length != 0) {
            return false;
        }
        tableStruct.setFieldName(existFieldName);
        tableStruct.setFieldType(existFieldType);
        tableStruct.setAssociationTable(existAssociationTable);
        // 更新redis缓存中的数据表结构信息
        dataModelRedisUtil.setValue(DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX+ DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.FILTER_PARTITION + query.getSaveAs(), tableStruct);
        return true;
    }

    @Override
    public boolean modifyTableStructWhenAlterTableField(AlterTableFieldHandlingQuery query) {
        TableStruct tableStruct = getSaveAsTableStruct(query.getDataModelName(), query.getSaveAs(),
                query.getTableName());
        Map<String, String> rename = query.getRename();
        for (Map.Entry<String, String> entry : rename.entrySet()) {
            this.renameField(tableStruct, entry.getKey(), entry.getValue());
        }
        // 更新redis缓存中的数据表结构信息
        dataModelRedisUtil.setValue(DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX+ DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.FILTER_PARTITION + query.getSaveAs(), tableStruct);
        return true;
    }

    @Override
    public boolean modifyTableStructWhenMerge(MergeTableHandlingQuery query, String childTableName) {
        log.debug("childTableName: {}", childTableName);
        if (StringUtils.isEmpty(childTableName)) {
            return false;
        }
        /* 分割字符串 */
        String[] childArr = DataUtil.splitStringOnFileSep(childTableName);
        /* 获取子表表结构信息 */
        TableStruct childTableStruct = dataCenterCommonServiceImpl.getTableStructInfoFromMongo(childArr[1], childArr[2], childArr[0]);
        if (childTableStruct == null) {
            return false;
        }
        /* 获取父表表结构信息 */
        TableStruct fatherTableStruct = getSaveAsTableStruct(query.getDataModelName(),  query.getTableName(),
                DataCenterConstant.FILTER_PARTITION);
        // 获取已存在关联表字段的字段名
        List<AssociationTable> existAssociationTables;
        if (fatherTableStruct.getAssociationTable() == null) {
            existAssociationTables = new ArrayList<>();
        } else {
            existAssociationTables = fatherTableStruct.getAssociationTable();
        }
        // 将父表表结构中已经存在的子表关联放入已存在的字段名中
        List<String> existFieldName = new ArrayList<>(fatherTableStruct.getFieldName());
        for (AssociationTable a : existAssociationTables) {
            existFieldName.add(a.getFieldName());
        }
        /* 获取子表的所有字段以及字段的类型 */
        Map<String, String> childFieldType = childTableStruct.getFieldType();
        List<AssociationTable> childAssociationTables = childTableStruct.getAssociationTable();
        for (AssociationTable a : childAssociationTables) {
            childFieldType.put(a.getFieldName(), a.getFieldType());
        }
        /* 将子表保留列的字段名和字段类型更新入父表的结构信息 */
        Map<String, String> reviseName = query.getReviseName();
        log.debug(reviseName.toString());
        AssociationTable associationTable;
        for (String str : query.getMaintainCol()) {
            /* 当关联字段与保留字段重复时,不需要替换保留字段的名称 */
            // java 中 list重写了equals方法 在AbstractList进行了重写
            if (!(query.getLeftOn().contains(str) && query.getLeftOn().equals(query.getRightOn()))) {
                associationTable = new AssociationTable();
                associationTable.setTableType(childArr[0]);
                associationTable.setDatasourceAlias(childArr[1]);
                associationTable.setTableName(childArr[2]);
                /* 判断字段名是否重复 */
                if (existFieldName.contains(str)) {
                    log.info("-------   replace {} : {}", str, reviseName.get(str));
                    // 若存在字段存在重复的名称, 则需要替换新的名称
                    associationTable.setFieldName(reviseName.get(str));
                } else {
                    associationTable.setFieldName(str);
                }
                associationTable.setFieldType(childFieldType.get(str));
                existAssociationTables.add(associationTable);
            }
        }
        fatherTableStruct.setAssociationTable(existAssociationTables);
        log.debug("新的父表表结构信息: {}", fatherTableStruct.toString());
        // 更新redis的表结构信息
        // 更新redis缓存中的数据表结构信息
        dataModelRedisUtil.setValue(DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX+ DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.FILTER_PARTITION + query.getSaveAs(), fatherTableStruct);
        return true;
    }

}


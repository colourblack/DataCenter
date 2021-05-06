package com.yingf.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.entity.DataWareHouseInfo;
import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.vo.PageResultVO;
import com.yingf.domain.vo.original.TableDataTypeVO;
import com.yingf.mapper.DataWareHouseInfoMapper;
import com.yingf.service.IDataCenterCommonService;
import com.yingf.util.DataModelRedisUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yingf Fangjunjin
 * @Description 数据中心通用Service实现类
 * @Date 2021/3/12
 */
@Service
public class DataCenterCommonServiceImpl implements IDataCenterCommonService {

    private final static Logger log = LoggerFactory.getLogger(DataCenterCommonServiceImpl.class);

    final DataWareHouseInfoMapper dataWareHouseInfoMapper;

    final MongoTemplate mongoDbOriginalTemplate;

    final MongoTemplate mongoDbFilterTemplate;

    final MongoTemplate mongoDbPivotTemplate;

    final DataModelRedisUtil dataModelRedisUtil;

    @Autowired
    public DataCenterCommonServiceImpl(DataWareHouseInfoMapper dataWareHouseInfoMapper, MongoTemplate mongoDbOriginalTemplate,
                                       MongoTemplate mongoDbFilterTemplate,
                                       MongoTemplate mongoDbPivotTemplate, DataModelRedisUtil dataModelRedisUtil) {
        this.dataWareHouseInfoMapper = dataWareHouseInfoMapper;
        this.mongoDbOriginalTemplate = mongoDbOriginalTemplate;
        this.mongoDbFilterTemplate = mongoDbFilterTemplate;
        this.mongoDbPivotTemplate = mongoDbPivotTemplate;
        this.dataModelRedisUtil = dataModelRedisUtil;
    }

    @Override
    public TableStruct getTableStructInfoFromMongo(String dataModelName, String tableName, String partitionType) {
        String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + tableName +
                DataCenterConstant.MONGO_COLLECTION_SEP + DataCenterConstant.TABLE_STRUCT_SUFFIX;
        log.debug("读取Mongo DB中 {} 的Table Struct", collectionName);
        // 查询时候不返回mongo db 自带的_id
        Document excludeMongoId = new Document("_id", 0);
        BasicQuery basicQuery = new BasicQuery(new Document(), excludeMongoId);
        JSONObject struct = null;
        switch (partitionType) {
            case DataCenterConstant.ORIGINAL_PARTITION:
                struct = mongoDbOriginalTemplate.findOne(basicQuery, JSONObject.class, collectionName);
                break;
            case DataCenterConstant.FILTER_PARTITION:
                struct = mongoDbFilterTemplate.findOne(basicQuery, JSONObject.class, collectionName);
                break;
            case DataCenterConstant.PIVOT_PARTITION:
                struct = mongoDbPivotTemplate.findOne(basicQuery, JSONObject.class, collectionName);
                break;
            default:
                break;
        }
        if (struct != null) {
            try {
                return JSONObject.toJavaObject(struct, TableStruct.class);
            } catch (JSONException e) {
                log.error("无法解析字符串");
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public int getTableTotalCount(String dataModelName, String tableName, String partitionType) {
        // 获取table struct
        TableStruct struct = getTableStructInfoFromMongo(dataModelName, tableName, partitionType);
        // 尝试从redis hash TABLE_STRUCT_HASH 中获取
        if (struct != null) {
            return struct.getCount();
        } else {
            log.error("无法获取 {}-{}-{} 的配置文件, 因此无法获取表总行数", partitionType, dataModelName, tableName);
            return 0;
        }
    }

    @Override
    public TableDataTypeVO getTableDataType(String dataModelName, String tableName, String partitionType) {
        // 获取table struct
        TableStruct tableStruct = getTableStructInfoFromMongo(dataModelName, tableName, partitionType);
        if (tableStruct != null) {
            log.debug("{}的表结构信息: {}", dataModelName, tableStruct.toString());
            TableDataTypeVO result = new TableDataTypeVO();
            result.setFieldName(tableStruct.getFieldName());
            result.setFieldType(tableStruct.getFieldType());
            return result;
        } else {
            log.warn("无法获取 {}-{}-{} 的配置文件", partitionType, dataModelName, tableName);
            return null;
        }
    }

    @Override
    public PageResultVO getTableRecords(String tableName, String dataModelName, int currPage, int pageSize, String partitionType) {
        // Collection name
        String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + tableName;
        log.debug("读取Mongo db中collection: {}", collectionName);
        List<JSONObject> records = new ArrayList<>();
        // 数据表总行数
        int totalCount = this.getTableTotalCount(dataModelName, tableName, partitionType);
        // 总页数
        int totalPage = totalCount / pageSize + 1;
        // 定位分页查询所在页的第一个record在数据表的位置
        int startIndex = (currPage - 1) * pageSize;
        if (startIndex > totalCount) {
            return new PageResultVO(totalCount, pageSize, totalPage, currPage, records);
        }
        Query query = new Query().skip(startIndex).limit(pageSize);


        switch (partitionType) {
            case DataCenterConstant.ORIGINAL_PARTITION:
                records = mongoDbOriginalTemplate.find(query, JSONObject.class, collectionName);
                break;
            case DataCenterConstant.FILTER_PARTITION:
                records = mongoDbFilterTemplate.find(query, JSONObject.class, collectionName);
                break;
            case DataCenterConstant.PIVOT_PARTITION:
                records = mongoDbPivotTemplate.find(query, JSONObject.class, collectionName);
                break;
            default:
                log.warn("请求参数有误, 分区名称为{}, 请检查数据分区的名称是否合法", partitionType);
                break;
        }


        return new PageResultVO(totalCount, pageSize, totalPage, currPage, records);
    }


    @Override
    public List<String> getTableList(String dataModelName, int currPage, int pageSize, String partitionType) {
        List<String> tableList = null;
        // 若当前分区为 original, 则直接通过mysql查询相关的信息
        if (DataCenterConstant.ORIGINAL_PARTITION.equals(partitionType)) {
            String list = dataWareHouseInfoMapper.selectOriginalTableName(dataModelName);
            tableList = Arrays.asList(list.split(","));
        }

        // 若当前分区为 filter, 则通过mongo db查询相关信息
        if (DataCenterConstant.FILTER_PARTITION.equals(partitionType)) {
            Set<String> collectionNames = mongoDbFilterTemplate.getCollectionNames();
            tableList = new ArrayList<>();
            for (String name : collectionNames) {
                if (name.startsWith(dataModelName)) {
                    if (!name.endsWith(DataCenterConstant.TABLE_STRUCT_SUFFIX) &&
                            !name.endsWith(DataCenterConstant.LOG_STRUCT_SUFFIX)) {
                        tableList.add(name.substring(name.indexOf(DataCenterConstant.MONGO_COLLECTION_SEP) + 1));
                    }
                }
            }
        }

        // 当查询的结果不为空值, 可以直接对list进行逻辑分页
        if (tableList != null) {
            // 若page size == -1, 则查询全表数据
            if (pageSize == -1) {
                return tableList;
            }
            int offset = (currPage - 1) * pageSize;
            tableList = tableList.stream().skip(offset).limit(pageSize).collect(Collectors.toList());
        }
        return tableList;
    }

    @Override
    public PageResultVO getDataSourceInfo(int currPage, int pageSize) {
        int offset = (currPage - 1) * pageSize;
        List<DataWareHouseInfo> sysCusDataInfos = dataWareHouseInfoMapper.selectDataInfo(offset, pageSize);
        int totalCount = dataWareHouseInfoMapper.selectTotalCount();
        return new PageResultVO(totalCount, pageSize, totalCount / pageSize + 1, currPage, sysCusDataInfos);
    }

    @Override
    public List<String> getAllModelName() {
        return dataWareHouseInfoMapper.selectAllModelName();
    }
}

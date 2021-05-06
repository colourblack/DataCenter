package com.yingf.database;

import com.alibaba.fastjson.JSONObject;
import com.yingf.constant.DataCenterConstant;
import com.yingf.constant.enums.DatabaseType;
import com.yingf.database.conn.ConnectionFactory;
import com.yingf.database.exec.ResultProcessor;
import com.yingf.database.exec.SqlExecutor;
import com.yingf.database.sql.GenericSqlGenerator;
import com.yingf.domain.entity.DataWareHouseDatabaseInfo;
import com.yingf.domain.vo.PageResultVO;
import com.yingf.util.DataModelRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Date 2021/3/4
 */
@Service
public class IDataBaseWorkerServiceImpl implements IDataBaseWorkerService {
    /**
     * 基本的执行流程:
     * 1. 获取connection
     * 2. 获取sql语句
     * 3. 生成sql语句执行器
     * 4. 获取执行结果ResultSet
     * 5. 通过结果处理器对执行结果ResultSet进行处理.
     */

    private static final Logger log = LoggerFactory.getLogger(IDataBaseWorkerServiceImpl.class);

    final ConnectionFactory connectionFactory;

    final GenericSqlGenerator sqlGenerator;

    final ResultProcessor resultProcessor;

    final DataModelRedisUtil redisUtil;

    final MongoTemplate mongoDbOriginalTemplate;

    final MongoTemplate mongoDbFilterTemplate;

    @Autowired
    public IDataBaseWorkerServiceImpl(ConnectionFactory connectionFactory, GenericSqlGenerator sqlGenerator,
                                      ResultProcessor resultProcessor, DataModelRedisUtil redisUtil,
                                      MongoTemplate mongoDbOriginalTemplate, MongoTemplate mongoDbFilterTemplate) {
        this.connectionFactory = connectionFactory;
        this.sqlGenerator = sqlGenerator;
        this.resultProcessor = resultProcessor;
        this.redisUtil = redisUtil;
        this.mongoDbOriginalTemplate = mongoDbOriginalTemplate;
        this.mongoDbFilterTemplate = mongoDbFilterTemplate;
    }

    @Override
    public boolean checkValidConnection(DataWareHouseDatabaseInfo databaseInfo) {
        DatabaseType databaseType = getDatabaseType(databaseInfo.getDatabaseType().toUpperCase());
        if (databaseType == null) {
            // 若是第三方数据库类型为null, 则返回null, 表示无法支持该类型数据库
            return false;
        }
        Connection connection = connectionFactory.getConnection(databaseInfo, databaseType);
        String sql = sqlGenerator.getValidConnectionSql(databaseType);
        SqlExecutor executor = null;
        try {
            executor = new SqlExecutor(connection);
            executor.exec(sql);
            return true;
        } catch (SQLException | NullPointerException e) {
            log.error("数据仓库 - 无法正确连接第三方数据库");
            log.error(e.getMessage());
        } finally {
            if (executor != null) {
                executor.release();
            }
            connectionFactory.releaseConn(connection);
        }
        return false;
    }

    @Override
    public List<String> getTableNameList(DataWareHouseDatabaseInfo databaseInfo) throws SQLException {
        List<String> tableNameList;
        // 获取第三方数据库的类型
        DatabaseType databaseType = getDatabaseType(databaseInfo.getDatabaseType().toUpperCase());
        if (databaseType == null) {
            // 若是第三方数据库类型为null, 则返回null, 表示无法支持该类型数据库
            return null;
        }
        Connection connection = connectionFactory.getConnection(databaseInfo, databaseType);
        String sql = sqlGenerator.getTableNameListSql(databaseType, databaseInfo.getDatabaseName());
        log.debug("{}类型数据库执行sql 语句: {}", databaseType.name(), sql);
        SqlExecutor executor = null;
        try {
            // 开始执行查询任务
            executor = new SqlExecutor(connection);
            ResultSet resultSet = executor.exec(sql);
            tableNameList = resultProcessor.getTableNameList(resultSet, databaseType);
        } finally {
            // 释放资源
            if (executor != null) {
                executor.release();
            }
            connectionFactory.releaseConn(connection);
        }


        return tableNameList;
    }

    @Override
    public PageResultVO getTableDetailPreview(DataWareHouseDatabaseInfo databaseInfo, String tableName) throws SQLException {
        int currPage = 1;
        int pageSize = 20;
        int totalCount;
        List<Map<String, Object>> tableDetail;

        // 获取第三方数据库的类型
        DatabaseType databaseType = getDatabaseType(databaseInfo.getDatabaseType().toUpperCase());
        if (databaseType == null) {
            // 若是第三方数据库类型为null, 则返回null, 表示无法支持该类型数据库
            return null;
        }


        // 开始执行查询任务
        Connection connection = connectionFactory.getConnection(databaseInfo, databaseType);
        ResultSet resultSet;
        SqlExecutor executor = null;
        String sqlForDetail = sqlGenerator.getTableDetailSql(databaseType, tableName);
        String sqlForTotalCount = sqlGenerator.getTableTotalCountSql(databaseType, tableName);

        try {
            executor = new SqlExecutor(connection);

            // 执行第一个查询, 获取table的前20条数据
            log.debug("{}类型数据库执行sql 语句: {}", databaseType.name(), sqlForDetail);
            resultSet = executor.exec(sqlForDetail);
            tableDetail = resultProcessor.getTableDetail(resultSet);

            // 执行第二个查询, 获取table的总数目
            log.debug("{}类型数据库执行sql 语句: {}", databaseType.name(), sqlForTotalCount);
            resultSet = executor.exec(sqlForTotalCount);
            totalCount = resultProcessor.getTableTotalCount(resultSet);
        } finally {
            // 释放资源
            if (executor != null) {
                executor.release();
            }
            connectionFactory.releaseConn(connection);
        }

        return new PageResultVO(totalCount, pageSize, totalCount / pageSize + 1, currPage, tableDetail);

    }


    @Override
    public boolean storeTableToMongoDb(DataWareHouseDatabaseInfo databaseInfo, List<String> tableNameList, String dataModelName) {

        // 获取第三方数据库的类型
        DatabaseType databaseType = getDatabaseType(databaseInfo.getDatabaseType().toUpperCase());
        if (databaseType == null) {
            // 若是第三方数据库类型为null, 则抛出异常 表示无法支持该类型数据库
            return  false;
        }

        // 开始执行查询任务
        Connection connection = connectionFactory.getConnection(databaseInfo, databaseType);
        ResultSet resultSet;
        SqlExecutor executor = null;
        // 标记位, 用于记录当前tableNameList的下标
        int index = 0;
        try {
            for (String tableName : tableNameList) {
                index++;
                String sql = sqlGenerator.getTableDetailSql(databaseType, tableName);
                executor = new SqlExecutor(connection);
                resultSet = executor.exec(sql);

                // 获取mongo db collection name
                String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + tableName;

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                List<String> fieldNames = new ArrayList<>();
                int columnCount = resultSetMetaData.getColumnCount();
                /* 获取列名 */
                for (int i = 1; i <= columnCount; i++) {
                    fieldNames.add(resultSetMetaData.getColumnName(i));
                }

                Map<String, Object> jsonObject;
                List<Map<String, Object>> records = new ArrayList<>();
                while (resultSet.next()) {
                    jsonObject = new JSONObject();
                    for (String fieldName : fieldNames) {
                        jsonObject.put(fieldName, resultSet.getObject(fieldName));
                    }
                    records.add(jsonObject);
                }
                log.debug("数据模块{}: - Table:{} -> 详细数据: {}", dataModelName, tableName, records.toString());
                mongoDbOriginalTemplate.insert(records, collectionName);
                mongoDbFilterTemplate.insert(records, collectionName);
            }
        } catch (SQLException e) {
            log.error("抽取第三方数据库进行存储时发生错误, 日志信息:{}", e.getMessage());
            for (int i = 0; i < index; i++) {
                // 获取mongo db collection name
                String collectionName = dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + tableNameList.get(i);
                mongoDbOriginalTemplate.dropCollection(collectionName);
                mongoDbFilterTemplate.dropCollection(collectionName);
            }
            return  false;

        } finally {
            if (executor != null) {
                executor.release();
            }
            connectionFactory.releaseConn(connection);
        }
        return true;
    }


    private DatabaseType getDatabaseType(String type) {
        DatabaseType databaseType = null;
        // 判断第三方数据库是否系统支持的类型
        for (DatabaseType t : DatabaseType.values()) {
            if (t.name().equals(type)) {
                databaseType = t;
                break;
            }
        }
        return databaseType;
    }

}

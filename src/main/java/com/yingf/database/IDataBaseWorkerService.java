package com.yingf.database;


import com.yingf.domain.entity.DataWareHouseDatabaseInfo;
import com.yingf.domain.vo.PageResultVO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yingf Fangjunjin
 * @Description 当第三方数据源为数据库时,
 *              使用该类为数据源连接提供服务
 * @Date 2021/3/4
 */
public interface IDataBaseWorkerService {

    /**
     * 检查该连接是否合法
     * @param databaseInfo 数据仓库基本信息
     * @return true valid - false invalid
     */
    boolean checkValidConnection(DataWareHouseDatabaseInfo databaseInfo);

    /**
     * 获取指定的第三方数据库中所有列表名
     * @param databaseInfo  第三方数据库的基本信息
     * @throws SQLException 向第三方数据库查询发生异常
     * @return 指定的第三方数据库的所有列表名
     */
    List<String> getTableNameList(DataWareHouseDatabaseInfo databaseInfo) throws SQLException;

    /**
     * 获取第三方数据库指定列表的预览信息
     * @param databaseInfo  第三方数据库的基本信息
     * @param tableName     指定列表名
     * @throws SQLException 向第三方数据库查询发生异常
     * @return 预览信息
     */
    PageResultVO getTableDetailPreview(DataWareHouseDatabaseInfo databaseInfo, String tableName) throws  SQLException;


    /**
     * 将第三方数据库的指定table的数据存入mongodb, 便于python进行数据清洗
     * @param databaseInfo   第三方数据库的基本信息
     * @param tableNameList  指定列表名的集合
     * @param dataModelName  当前数据模块的名称
     * @return  true success - false failed
     */
    boolean storeTableToMongoDb(DataWareHouseDatabaseInfo databaseInfo, List<String> tableNameList, String dataModelName);
}

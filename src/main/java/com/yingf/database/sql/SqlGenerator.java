package com.yingf.database.sql;

/**
 * @author yingf Fangjunjin
 * @Description sql语句生成器接口
 * @Date 2021/3/5
 */
public interface SqlGenerator {

    /**
     * 返回用于测试连接的sql语句
     * @return sql语句
     */
    String checkValidConnectionSql();


    /**
     * 获取第三方数据库中表格的sql语句;
     * @param databaseName 数据库名称
     * @return sql语句
     */
    String getTableNameListSql(String databaseName);


    /**
     * 查询第三方数据库的指定表的前20条数据
     * @param tableName 目标table name
     * @return 目标table的前20条数据 SQL语句
     */
    String getTableDetailPreview(String tableName);


    /**
     * 查询第三方数据库指定表格的总行数
     * @param tableName 目标table
     * @return table total count SQL语句
     */
    String getTableTotalCount(String tableName);


    /**
     * 获取第三方数据库指定表格的所有数据
     * @param tableName 目标table
     * @return SQL语句
     */
    String getAllRecords(String tableName);



}

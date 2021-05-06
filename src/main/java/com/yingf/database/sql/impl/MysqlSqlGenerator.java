package com.yingf.database.sql.impl;

import com.yingf.database.sql.SqlGenerator;
import org.springframework.stereotype.Component;

/**
 * @author yingf Fangjunjin
 * @Description Mysql - SQL语句生成器
 * @Date 2021/3/5
 */
@Component
public class MysqlSqlGenerator implements SqlGenerator {

    @Override
    public String checkValidConnectionSql() {
        return "select 1;";
    }

    @Override
    public String getTableNameListSql(String databaseName) {
        return "select table_name from information_schema.tables where table_schema = '" + databaseName + "' ;";
    }

    @Override
    public String getTableDetailPreview(String tableName) {
        return "select * from " + tableName + " limit 0, 20;";
    }

    @Override
    public String getTableTotalCount(String tableName) {
        return "select count(*) count from " + tableName + " ;";
    }

    @Override
    public String getAllRecords(String tableName) {
        return "select * from " + tableName + " ;";
    }
}

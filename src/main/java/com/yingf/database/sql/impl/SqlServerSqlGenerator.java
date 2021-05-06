package com.yingf.database.sql.impl;

import com.yingf.database.sql.SqlGenerator;
import org.springframework.stereotype.Component;


/**
 * @author yingf Fangjunjin
 * @Description SqlServer的Sql语句生成器
 * @Date 2021/3/8
 */
@Component
public class SqlServerSqlGenerator implements SqlGenerator {
    @Override
    public String checkValidConnectionSql() {
        return "select 1;";
    }

    @Override
    public String getTableNameListSql(String databaseName) {
        return "SELECT name FROM " + databaseName + "..SysObjects  Where xtype='U' and name like '%C%' ORDER BY Name;";
    }

    @Override
    public String getTableDetailPreview(String tableName) {
        return "select top 20 * from " + tableName + ";" ;

    }

    @Override
    public String getTableTotalCount(String tableName) {
        return "select count(*) as count from " + tableName + ";";
    }

    @Override
    public String getAllRecords(String tableName) {
        return "select * from " + tableName + " ;";
    }
}

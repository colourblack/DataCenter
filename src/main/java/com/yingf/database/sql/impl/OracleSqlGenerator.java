package com.yingf.database.sql.impl;

import com.yingf.database.sql.SqlGenerator;
import org.springframework.stereotype.Component;

/**
 * @author yingf Fangjunjin
 * @Description Oracle的Sql语句生成器
 * @Date 2021/3/8
 */
@Component
public class OracleSqlGenerator implements SqlGenerator {
    @Override
    public String checkValidConnectionSql() {
        return "select 'x' from dual;";
    }

    @Override
    public String getTableNameListSql(String databaseName) {
        return "select * from user_tables;";
    }

    @Override
    public String getTableDetailPreview(String tableName) {
        return "select * from" + tableName +" where ROWNUM < 4;";
    }

    @Override
    public String getTableTotalCount(String tableName) {
        return "select count(*) count from " + tableName + ";" ;
    }

    @Override
    public String getAllRecords(String tableName) {
        return "select * from " + tableName + " ;";
    }
}

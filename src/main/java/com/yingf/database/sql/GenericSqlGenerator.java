package com.yingf.database.sql;

import com.yingf.constant.enums.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 通用Sql语句生成器
 * @Date 2021/3/4
 */
@Component
public class GenericSqlGenerator {

    private final Map<DatabaseType, SqlGenerator> sqlGeneratorMap = new HashMap<>();

    final SqlGenerator mysqlSqlGenerator;

    final SqlGenerator oracleSqlGenerator;

    final SqlGenerator sqlServerSqlGenerator;

    @Autowired
    public GenericSqlGenerator(SqlGenerator mysqlSqlGenerator, SqlGenerator oracleSqlGenerator, SqlGenerator sqlServerSqlGenerator) {
        this.mysqlSqlGenerator = mysqlSqlGenerator;
        this.oracleSqlGenerator = oracleSqlGenerator;
        this.sqlServerSqlGenerator = sqlServerSqlGenerator;
        sqlGeneratorMap.put(DatabaseType.MYSQL, mysqlSqlGenerator);
        sqlGeneratorMap.put(DatabaseType.ORACLE, oracleSqlGenerator);
        sqlGeneratorMap.put(DatabaseType.SQLSERVER, sqlServerSqlGenerator);
    }

    public String getValidConnectionSql(DatabaseType databaseType) {
        return sqlGeneratorMap.get(databaseType).checkValidConnectionSql();
    }

    public String getTableNameListSql(DatabaseType databaseType, String databaseName) {
        return sqlGeneratorMap.get(databaseType).getTableNameListSql(databaseName);
    }

    public String getTableDetailSql(DatabaseType databaseType, String tableName) {
        return sqlGeneratorMap.get(databaseType).getTableDetailPreview(tableName);
    }

    public String getTableTotalCountSql(DatabaseType databaseType, String tableName) {
        return sqlGeneratorMap.get(databaseType).getTableTotalCount(tableName);
    }


}

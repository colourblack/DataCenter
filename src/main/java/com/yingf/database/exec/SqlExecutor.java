package com.yingf.database.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yingf Fangjunjin
 * @Description Sql 语句执行器
 * @Date 2021/3/5
 */
public class SqlExecutor {

    private static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

    private final Statement statement;

    private ResultSet resultSet;

    public SqlExecutor(Connection connection) throws SQLException, NullPointerException {
        statement = connection.createStatement();
    }


    /**
     * 通用的SQL语句执行器
     * @param sql           待执行的sql语句
     * @return              执行结果集
     * @throws SQLException 若发生错误, 则向外抛出异常
     */
    public ResultSet exec(String sql) throws SQLException {
        resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public void release() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("The ResultSet could not close correctly");
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("The Statement could not close correctly");
                e.printStackTrace();
            }
        }
    }

}

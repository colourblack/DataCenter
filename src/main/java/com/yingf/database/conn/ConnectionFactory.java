package com.yingf.database.conn;

import com.yingf.constant.enums.DatabaseType;
import com.yingf.domain.entity.DataWareHouseDatabaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author yingf Fangjunjin
 * @Description 根据不同的第三方数据源, 生成不同的Database类型
 * @Date 2021/3/8
 */
@Component
public class ConnectionFactory {

    private final static Logger log = LoggerFactory.getLogger(ConnectionFactory.class);

    /**
     * 根据不同的第三方数据库类型获取对应的Connection
     *
     * @param databaseInfo 第三方数据库的连接信息
     * @return connection or null
     */
    public Connection getConnection(DataWareHouseDatabaseInfo databaseInfo, DatabaseType databaseType) {
        // 根据第三方数据源的信息, 获取相应的connection
        Connection connection = null;
        String url = null;
        String driverName = null;
        switch (databaseType) {
            case MYSQL:
                driverName = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://" + databaseInfo.getDatabaseIp() + ":" + databaseInfo.getDatabasePort() + "/"
                        + databaseInfo.getDatabaseName()
                        + "?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=GMT";
                break;
            case ORACLE:
                driverName = "oracle.jdbc.driver.OracleDriver";
                url = "jdbc:oracle:thin:@" + databaseInfo.getDatabaseIp() + ":" + databaseInfo.getDatabasePort()
                        + ":" + databaseInfo.getDatabaseName();
                break;
            case SQLSERVER:
                driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                url = "jdbc:sqlserver://" + databaseInfo.getDatabaseIp() + ":" + databaseInfo.getDatabasePort() +
                        ";database=" + databaseInfo.getDatabaseName();
                break;
            default:
                break;
        }
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, databaseInfo.getDatabaseUsername(), databaseInfo.getDatabasePassword());
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.getMessage());
        }
        return connection;
    }

    public void releaseConn(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("The Connection could not close correctly");
                e.printStackTrace();
            }
        }
    }

}

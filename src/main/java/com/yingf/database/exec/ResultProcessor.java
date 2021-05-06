package com.yingf.database.exec;

import com.yingf.constant.enums.DatabaseType;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author yingf Fangjunjin
 * @Description 从第三方数据库获取数据
 * @Date 2021/3/8
 */
@Component
public class ResultProcessor {

    public List<String> getTableNameList(ResultSet rs, DatabaseType type) throws SQLException {
        List<String> list = new ArrayList<>();
        switch (type) {
            case MYSQL:
                while (rs.next()) {
                    list.add(rs.getString("table_name"));
                }
                break;
            case ORACLE:
                while (rs.next()) {
                    list.add(rs.getString("TABLE_NAME"));
                }
                break;
            case SQLSERVER:
                while (rs.next()) {
                    list.add(rs.getString("name"));
                }
                break;
            default:
                break;
        }
        return list;
    }


    public List<Map<String, Object>> getTableDetail(ResultSet rs) throws SQLException {
        // 结果集
        List<Map<String, Object>> result = new ArrayList<>();

        // 用于存储查询table中的列名
        List<String> columnName = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        // 获取列数
        int columnCount = resultSetMetaData.getColumnCount();
        // 获取列名
        for (int i = 0; i < columnCount; i++) {
            columnName.add(resultSetMetaData.getColumnName(i + 1));
        }

        // 获取结果集
        while (rs.next()) {
            // 用Map与table中每一行做映射 key -> columnName , value -> value
            Map<String, Object> dataMap = new HashMap<>(2);
            for (int i = 0; i < columnCount; i++) {
                // 将表中列名 与其对应的值 用Map进行映射存储
                dataMap.put(columnName.get(i), rs.getObject(i + 1));
            }
            result.add(dataMap);
        }

        // 查询的table为空表, 则需要做一次列名的空映射并且保存传递给前端
        if (result.isEmpty()) {
            Iterator<String> iterator = columnName.iterator();
            Map<String, Object> dataMap = new HashMap<>(2);
            while (iterator.hasNext()) {
                dataMap.put(iterator.next() + "", "");
            }
            result.add(dataMap);
        }

        return result;
    }


    public int getTableTotalCount(ResultSet rs) throws SQLException {
        int totalNum = 0;
        while (rs.next()) {
            totalNum = Integer.parseInt(rs.getString("COUNT"));
        }
        return totalNum;
    }

}

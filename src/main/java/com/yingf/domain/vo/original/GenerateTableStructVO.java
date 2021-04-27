package com.yingf.domain.vo.original;

import java.io.Serializable;
import java.util.List;

/**
 * @author yingf Fangjunjin
 * @Description 消息传递对象, 用于通知python进行数据类型的分析
 * @Date 2021/3/12
 */
public class GenerateTableStructVO implements Serializable {

    private Long userId;

    private String dataModelName;

    private List<String> tableNameList;

    private String handleType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public List<String> getTableNameList() {
        return tableNameList;
    }

    public void setTableNameList(List<String> tableNameList) {
        this.tableNameList = tableNameList;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    @Override
    public String toString() {
        return "GenerateTableStructVO{" +
                "userId=" + userId +
                ", dataModelName='" + dataModelName + '\'' +
                ", tableNameList=" + tableNameList +
                ", handleType='" + handleType + '\'' +
                '}';
    }
}

package com.yingf.domain.query.clean;
/**
 * @author yingf Fangjunjin
 * @Description 描述了执行清洗任务应获取的基本信息的接口
 * @Date 2021/3/16
 */
public interface CleaningQuery {

    /**
     * 获取此次操作的用户id
     * @return 返回用户id
     */
    long getUserId();

    /**
     * 获取此次操作的数据源模型名称
     * @return 返回此次操作的数据源模型名称
     */
    String getDataModelName();

    /**
     * 获取此次操作的数据表名称
     * @return 返回此次操作的table name
     */
    String getTableName();

    /**
     * 获取此次操作的另存为名称
     * 另存为名称不能为空, 若不需要另存为,
     * 则 tableName == saveAs
     * @return 返回操作此次另存的名称
     */
    String getSaveAs();
}

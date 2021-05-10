package com.yingf.service;


import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.query.clean.*;
import com.yingf.domain.vo.original.TableDataTypeVO;

/**
 * @author yingf Fangjunjin
 * @Description 负责数据清洗分区的Service
 * @Date 2021/3/16
 */
public interface IDataCenterCleaningService {

    /**
     * 获取清洗任务的日志信息
     * @param dataModelName 指定数据源名称
     * @param tableName     指定的数据表名称
     * @return 日志过滤信息
     */
    String getLogInfo(String dataModelName, String tableName);


    /**
     * 在清洗分区中, 从redis中获取指定表的字段信息
     * @param dataModelName 指定数据源名称
     * @param tableName     指定的数据表名称
     * @param saveAs        数据表的另存为名称
     * @return 返回指定数据表的filed name和filed type
     */
    TableDataTypeVO getTableDataTypeFromRedis(String dataModelName, String tableName, String saveAs);

    /**
     * 在清洗分区中, 获取指定的表的结构信息
     * @param dataModelName 指定数据源名称
     * @param tableName     指定的数据表名称
     * @param saveAs        数据表的另存为名称
     * @return 表结构信息, table struct information
     */
    TableStruct getSaveAsTableStruct(String dataModelName, String tableName, String saveAs);


    /**
     * 检测该任务是否为合法的任务
     * @param query 请求的参数
     * @return true - false
     */
    boolean checkAvailableTask(CleaningQuery query);


    /**
     * 向kafka发送任务信息
     * @param query 请求的参数
     * @return success or failed
     */
    String sendTaskMessage(CleaningQuery query);

    /**
     * 对数据表进行字段修改时, 需要留意重命名的问题
     * @param tableStruct 数据表的表结构信息
     * @param oldName     字段 - 旧名称
     * @param newName     字段 - 新名称
     * @return 重命名字段后的数据表结构信息
     */
    TableStruct renameField (TableStruct tableStruct, String oldName, String newName);


    /**
     * 当数据清洗操作新增字段时, 需要对该表的表结构信息进行调整
     * @param query 字段修改的请求参数
     * @return 是否成功修改redis中的表结构信息
     */
    boolean modifyTableStructWhenAddCol(AddColHandlingQuery query);


    /**
     * 当数据清洗对字段进行删除时, 需要对该表的表结构信息进行调整
     * @param query 字段修改的请求参数
     * @return 是否成功修改redis中的表结构信息
     */
    boolean modifyTableStructWhenDelCol(DelColHandlingQuery query);

    /**
     * 当数据清洗对字段进行修改时, 需要对该表的表结构信息进行调整
     * @param query 字段修改的请求参数
     * @return 是否成功修改redis中的表结构信息
     */
    boolean modifyTableStructWhenAlterTableField(AlterTableFieldHandlingQuery query);

    /**
     * 当数据清洗执行多表关联操作时, 对表结构信息进行调整更新
     * @param query          多表关联请求参数
     * @param childTableName 关联子表名称
     * @return 是否成功更新数据表结构信息
     */
    boolean modifyTableStructWhenMerge(MergeTableHandlingQuery query, String childTableName);

}

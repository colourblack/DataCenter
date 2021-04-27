package com.yingf.service;


import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.vo.original.TableDataTypeVO;

/**
 * @author yingf Fangjunjin
 * @Description 负责数据清洗分区的Service
 * @Date 2021/3/16
 */
public interface IDataCenterCleaningService {


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

}

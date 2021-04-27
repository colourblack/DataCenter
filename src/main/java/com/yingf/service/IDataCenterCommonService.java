package com.yingf.service;


import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.vo.PageResultVO;
import com.yingf.domain.vo.original.TableDataTypeVO;

import java.util.List;

/**
 * @author yingf Fangjunjin
 * @Description 数据中心通用Service接口
 * @Date 2021/3/12
 */
public interface IDataCenterCommonService {

    /**
     * 获取数据源信息简介
     * @param currPage 当前页数
     * @param pageSize 页面大小
     * @return data source information
     */
    PageResultVO getDataSourceInfo(int currPage, int pageSize);

    /**
     * 在Mongo db上获取table struct information
     * @param dataModelName  数据源别名
     * @param tableName      表名
     * @param partitionType  表类型
     * @return 表结构信息
     */
    TableStruct getTableStructInfoFromMongo(String dataModelName, String tableName, String partitionType);

    /**
     * 获取表(集合)总行数
     * @param dataModelName  当前数据模块名称
     * @param tableName      表名
     * @param partitionType  当前模块类型
     * @return total count or zero when wrongs happened
     */
    int getTableTotalCount(String dataModelName, String tableName, String partitionType);

    /**
     * 通过指定的数据模块和表格名获取该表的字段名以及对应的数据类型
     * @param dataModelName 当前数据模块名称
     * @param tableName     表名
     * @param partitionType 当前模块类型
     * @return 表格结构信息
     */
    TableDataTypeVO getTableDataType(String dataModelName, String tableName, String partitionType);

    /**
     * 通过指定的分区, 当前模块名和数据表表名获取对应的数据表详细信息
     * @param tableName       表名(集合名称)
     * @param dataModelName   当前数据模块名称
     * @param currPage        分页当前页
     * @param pageSize        分页页大小
     * @param partitionType   表类型
     * @return 分页读取结果指定的数据表详细信息
     */
    PageResultVO getTableRecords(String tableName, String dataModelName, int currPage, int pageSize, String partitionType);


    /**
     * 通过指定的分区读取和数据模块名称获取该分区模块的所有列表名
     * @param dataModelName   当前数据模块名称
     * @param currPage        分页当前页
     * @param pageSize        分页页大小
     * @param partitionType   表类型
     * @return 分页读取结果指定分区模块的Table Name List
     */
    List<String> getTableList(String dataModelName, int currPage, int pageSize, String partitionType);


    /**
     * 查询所有模块名称
     * @return 返回所有模块名称
     */
    List<String> getAllModelName();

}

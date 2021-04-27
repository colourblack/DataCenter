package com.yingf.service;

import com.yingf.project.datacenter.domain.DataWareHouseDatabaseInfo;
import com.yingf.project.datacenter.vo.PageResultVO;
import com.yingf.project.datacenter.vo.original.ExcelPreviewInfoVO;
import com.yingf.project.datacenter.vo.original.SheetDataPreviewVO;

import java.util.List;


/**
 * @author yingf Fangjunjin
 * @Description 数据仓库 - 用于生成数据源的服务类
 * @Date 2021/3/3
 */
public interface IDataSourceGeneratorService {

    /**
     * 校验数据模型的名称是否存在
     * @param dataModelName 待校验的数据模型名称
     * @param userId 用户ID
     * @return true - 存在, false - 不存在
     */
    boolean hasDataModelName(String dataModelName, long userId);


    /**
     * 当第三方数据源类型为数据库时, 测试数据库连接并且进行保存相关信息
     * @param dataWareHouseDatabaseInfo 第三方数据库信息
     * @return true - 成功连接, false - 连接失败
     */
    boolean testAndSetDatabaseCache(DataWareHouseDatabaseInfo dataWareHouseDatabaseInfo);


    /**
     * 分页查询第三方数据库的列表表名集合
     * 若当前查询不是根据关键字进行模糊查询, 则keyword = null
     * @param dataModelName 数据模型的名称
     * @param currPage      分页当前页
     * @param pageSize      分页页大小
     * @param keyword       关键字
     * @return 分页结果
     */
    PageResultVO getDbTableNameList(String dataModelName, int currPage, int pageSize, String keyword);


    /**
     * 查询第三方数据库指定表格的预览信息,
     * 用户通过预览信息能够对数据的准确性进行基本的判断.
     *
     * 预览信息:
     *  1. 前20条信息
     *  2. 总数目
     *
     * @param dataModelName 当前数据模型的名称
     * @param tableName     指定查询的table name
     * @return 查询数据
     */
    PageResultVO getDbTableDetailPreview(String dataModelName, String tableName);


    /**
     * 若缓存中不存在指定Excel文件的预览信息, 则尝试解析获取
     * @param fileMd5  Excel文件的MD5值
     * @param redisKey 用于缓存Excel文件预览信息的redis key
     * @return null or Excel文件预览信息
     */
    ExcelPreviewInfoVO parseExcel(String fileMd5, String redisKey);


    /**
     * 获取指定md5的Excel文件中的sheet name list
     * @param fileMd5        Excel文件的MD5值
     * @param currPage      分页当前页
     * @param pageSize      分页页大小
     * @return null or SheetNameList
     */
    PageResultVO getExcelSheetList(String fileMd5, int currPage, int pageSize);


    /**
     * 通过给定的ExcelSheetName, 获取相应的sheet表格预览信息
     * @param fileMd5        Excel文件的MD5值
     * @param sheetName      Excel中指定的sheet
     * @return null or data preview
     */
    SheetDataPreviewVO getExcelSheetPreview(String fileMd5, String sheetName);


    /**
     * 存储第三方数据到本地mongo db
     * @param dataModelName  当前数据模型的名称
     * @param tableNameList  第三方数据库中需要进行存储的列表名集合
     * @param userId         当前用户id
     * @param userName       当前用户名
     * @return success - true , failed - false
     */
    boolean storeThirdPartyDatabaseData(String dataModelName, List<String> tableNameList, long userId, String userName);


    /**
     * 存储第三方数据到本地mongo db
     * @param dataModelName  当前数据模型的名称
     * @param fileMd5        当前Excel文件的MD5值
     * @param sheetNameList  第三方数据库中需要进行存储的列表名集合
     * @param userId         当前用户id
     * @param userName       当前用户名
     * @return success - true , failed - false
     */
    boolean storeThirdPartyExcelData(String dataModelName, String fileMd5, List<String> sheetNameList, long userId, String userName);




}

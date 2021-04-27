package com.yingf.service.impl;

import com.yingf.mapper.DataWareHouseDatabaseInfoMapper;
import com.yingf.mapper.DataWareHouseFileInfoMapper;
import com.yingf.mapper.DataWareHouseInfoMapper;
import com.yingf.mapper.FileUploadInfoMapper;
import com.yingf.service.IDataSourceGeneratorService;
import com.yingf.util.DataModelRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author yingf Fangjunjin
 * @Description 数据建模 - 负责生成
 * @Date 2021/3/3
 */
@Service
public class DataSourceGeneratorServiceImpl implements IDataSourceGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DataSourceGeneratorServiceImpl.class);

    final DataModelRedisUtil redisUtil;

    final IDataBaseWorkerService dataBaseWorkerServiceImpl;

    final DataWareHouseInfoMapper dataWareHouseInfoMapper;

    final DataWareHouseDatabaseInfoMapper dataWareHouseDatabaseInfoMapper;

    final DataWareHouseFileInfoMapper dataWareHouseFileInfoMapper;

    final FileUploadInfoMapper fileUploadInfoMapper;

    final ExcelParseProcessor excelParseProcessor;

    final AsyncStoreThirdPartyData asyncStoreThirdPartyData;

    @Autowired
    public DataSourceGeneratorServiceImpl(DataModelRedisUtil redisUtil, IDataBaseWorkerService dataBaseWorkerServiceImpl,
                                          DataWareHouseInfoMapper dataWareHouseInfoMapper, DataWareHouseDatabaseInfoMapper dataWareHouseDatabaseInfoMapper,
                                          DataWareHouseFileInfoMapper dataWareHouseFileInfoMapper, FileUploadInfoMapper fileUploadInfoMapper,
                                          ExcelParseProcessor excelParseProcessor, AsyncStoreThirdPartyData asyncStoreThirdPartyData) {
        this.redisUtil = redisUtil;
        this.dataBaseWorkerServiceImpl = dataBaseWorkerServiceImpl;
        this.dataWareHouseInfoMapper = dataWareHouseInfoMapper;
        this.dataWareHouseDatabaseInfoMapper = dataWareHouseDatabaseInfoMapper;
        this.dataWareHouseFileInfoMapper = dataWareHouseFileInfoMapper;
        this.fileUploadInfoMapper = fileUploadInfoMapper;
        this.excelParseProcessor = excelParseProcessor;
        this.asyncStoreThirdPartyData = asyncStoreThirdPartyData;
    }

    @Override
    public boolean hasDataModelName(String dataModelName, long userId) {
        boolean result = existDataModelName(dataModelName);
        if (!result) {
            // 若该数据模型的名称不存在, 则将这个名称进行缓存
            redisUtil.setValueTimeout(DataCenterConstant.REDIS_DATA_WARE_HOUSE_NAME_PREFIX + dataModelName,
                    userId, 15 * 60);
        }
        return result;
    }

    /**
     * 查询缓存或数据库中是否存在该数据模块名称
     *
     * @param dataModelName 数据模块名称
     * @return 存在 - true, 不存在 -false
     */
    private boolean existDataModelName(String dataModelName) {
        // 判断缓存中是否存在该数据源名称的key
        String redisKey = DataCenterConstant.REDIS_DATA_WARE_HOUSE_NAME_PREFIX + dataModelName;
        if (redisUtil.existsKey(redisKey)) {
            return true;
        }
        // 判断数据库中是否存在该数据源名称
        if (dataWareHouseInfoMapper.selectOneByDataModelName(dataModelName) != null) {
            return true;
        }
        return false;
    }


    @Override
    public boolean testAndSetDatabaseCache(DataWareHouseDatabaseInfo databaseInfo) {
        boolean testResult;
        testResult = dataBaseWorkerServiceImpl.checkValidConnection(databaseInfo);
        log.debug("test datasource connection : {}", testResult);
        if (testResult) {
            // 连接测试成功以后，将对应的数据源信息保存到redis，
            // key : THIRD_PARTY_DATABASE_INFO-{dataModelName}
            String redisKey = DataCenterConstant.REDIS_THIRD_PARTY_DATABASE_PREFIX + databaseInfo.getDataModelName();
            redisUtil.setValueTimeout(redisKey, databaseInfo, 12 * 60);
        }
        return testResult;
    }

    @Override
    public PageResultVO getDbTableNameList(String dataModelName, int currPage, int pageSize, String keyword) {
        /* 该方法中的分页采用逻辑分页, 避免多次访问第三方数据库 */

        // 判断当前查询是否为根据关键字模糊搜索
        boolean keywordSearch = false;
        if (keyword != null && !keyword.trim().isEmpty()) {
            keywordSearch = true;
        }
        boolean success = true;
        // redis中用于存储第三方数据库中所有列表名的redis key
        String tableNameListRedisKey = DataCenterConstant.REDIS_THIRD_PARTY_DATABASE_TABLE_LIST + dataModelName;
        // List的起始下标
        long start = (currPage - 1) * pageSize;
        // List的结束下标
        long end = currPage * pageSize;
        // List中数目
        int totalCount = 0;
        // 分页保存的result list
        List<String> result = new ArrayList<>();

        if (redisUtil.existsKey(tableNameListRedisKey)) {
            // 若是redis中有缓存对应的TableNameList, 则从redis中获取数据
            List<Object> objectList;
            if (keywordSearch) {
                // 当本次查询为模糊搜索时 （start = 0，end = -1表示获取全部元素）
                objectList = redisUtil.listGet(tableNameListRedisKey, 0, -1);
                result = objectList.stream().map(Object::toString).collect(Collectors.toList());

                result = fuzzySearch(keyword, result);
                // 总表数
                totalCount = result.size();

                result = result.stream().skip(start).limit(pageSize).collect(Collectors.toList());
            } else {
                // 若不是模糊搜索, 则直接根据游标获取相应的table name
                objectList = redisUtil.listGet(tableNameListRedisKey, start, end);
                result = objectList.stream().map(Object::toString).collect(Collectors.toList());
                // 总表数
                totalCount = Math.toIntExact(redisUtil.listLength(tableNameListRedisKey));
            }
        } else {
            // 尝试从第三方数据库直接获取数据
            // 从redis中获取本次查询的第三方数据库基本信息
            String databaseInfoRedisKey = DataCenterConstant.REDIS_THIRD_PARTY_DATABASE_PREFIX + dataModelName;
            DataWareHouseDatabaseInfo databaseInfo = (DataWareHouseDatabaseInfo) redisUtil.getValue(databaseInfoRedisKey);
            if (databaseInfo == null) {
                success = false;
                log.error("无法在redis缓存中获取数据仓库模块{}的基本信息", dataModelName);
            } else {
                try {
                    // 一次查询该第三方库的所有列表名
                    List<String> tableNameList = dataBaseWorkerServiceImpl.getTableNameList(databaseInfo);
                    if (tableNameList != null) {

                        // 若成功查询, 则将结果进行缓存
                        redisUtil.listPushAll(tableNameListRedisKey,
                                tableNameList.stream().map(str -> (Object) str).collect(Collectors.toList()));
                        redisUtil.expire(tableNameListRedisKey, 10 * 60);
                        // 对总结果进行剪枝, 需要判断当前查询是否为模糊查询
                        if (keywordSearch) {

                            result = fuzzySearch(keyword, tableNameList);
                            // 总表数
                            totalCount = result.size();

                            result = result.stream().skip(start).limit(pageSize).collect(Collectors.toList());
                        } else {

                            result = tableNameList.stream().skip(start).limit(pageSize).collect(Collectors.toList());
                            // 总表数
                            totalCount = tableNameList.size();
                        }
                    }
                } catch (SQLException e) {
                    log.error("数据仓库模块{} - 无法获取数据库{}的数据表集合", dataModelName, databaseInfo.getDatabaseName());
                    log.error(e.getMessage());
                    success = false;
                }
            }
        }

        if (success) {
            log.info("成功获取数据仓库模块{}的列表信息", dataModelName);
            // 总页数
            int totalPage = totalCount / pageSize + 1;
            return new PageResultVO(totalCount, pageSize, totalPage, currPage, result);
        } else {
            return null;
        }
    }


    /**
     * 在List中进行模糊搜索
     *
     * @param keyword 关键字
     * @param list    列表
     * @return 模糊搜索结果
     */
    private List<String> fuzzySearch(String keyword, List<String> list) {
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        List<String> res = new ArrayList<>();
        Matcher matcher;
        for (String str : list) {
            matcher = pattern.matcher(str);
            if (matcher.find()) {
                res.add(str);
            }
        }
        log.debug(res.toString());
        return res;
    }


    @Override
    public PageResultVO getDbTableDetailPreview(String dataModelName, String tableName) {
        // 从redis中获取本次查询的第三方数据库基本信息
        String databaseInfoRedisKey = DataCenterConstant.REDIS_THIRD_PARTY_DATABASE_PREFIX + dataModelName;
        DataWareHouseDatabaseInfo databaseInfo = (DataWareHouseDatabaseInfo) redisUtil.getValue(databaseInfoRedisKey);
        if (databaseInfo == null) {
            log.error("无法在redis缓存中获取数据仓库模块{}的基本信息", dataModelName);
            return null;
        }

        PageResultVO pageResultVO = null;
        try {
            pageResultVO = dataBaseWorkerServiceImpl.getTableDetailPreview(databaseInfo, tableName);
        } catch (SQLException e) {
            log.warn("向第三方数据库{}查询指定表{}的预览信息时发生错误", dataModelName, tableName);
            log.error(e.getMessage());
        }
        return pageResultVO;
    }

    @Override
    public PageResultVO getExcelSheetList(String fileMd5, int currPage, int pageSize) {
        String redisKey = DataCenterConstant.REDIS_THIRD_PARTY_EXCEL_PREVIEW_INFO + fileMd5;
        ExcelPreviewInfoVO excelPreviewInfoVO = (ExcelPreviewInfoVO) redisUtil.getValue(redisKey);
        if (excelPreviewInfoVO == null) {
            excelPreviewInfoVO = parseExcel(fileMd5, redisKey);
            if (excelPreviewInfoVO == null) {
                return null;
            }
        }

        List<String> sheetNameList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : excelPreviewInfoVO.getSheetNameMap().entrySet()) {
            sheetNameList.add(entry.getValue());
        }
        int totalCount = sheetNameList.size();
        int totalPage = totalCount / pageSize + 1;
        sheetNameList = sheetNameList.stream().skip((currPage - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        return new PageResultVO(totalCount, pageSize, totalPage, currPage, sheetNameList);
    }

    @Override
    public SheetDataPreviewVO getExcelSheetPreview(String fileMd5, String sheetName) {
        String redisKey = DataCenterConstant.REDIS_THIRD_PARTY_EXCEL_PREVIEW_INFO + fileMd5;
        ExcelPreviewInfoVO excelPreviewInfoVO = (ExcelPreviewInfoVO) redisUtil.getValue(redisKey);
        if (excelPreviewInfoVO == null) {
            excelPreviewInfoVO = parseExcel(fileMd5, redisKey);
        }
        if (excelPreviewInfoVO == null) {
            return null;
        }

        List<String> sheetNameList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : excelPreviewInfoVO.getSheetNameMap().entrySet()) {
            sheetNameList.add(entry.getValue());
        }
        if (!sheetNameList.contains(sheetName)) {
            log.warn("指定的Excel文件中不包含sheet:{}, 请检查sheetName是否合法", sheetName);
            return null;
        }
        SheetDataPreviewVO sheetDataPreviewVO = new SheetDataPreviewVO();
        sheetDataPreviewVO.setHeader(excelPreviewInfoVO.getHeader().get(sheetName));
        sheetDataPreviewVO.setErrorRecord(excelPreviewInfoVO.getErrorRecords().get(sheetName).size());
        sheetDataPreviewVO.setRecord(excelPreviewInfoVO.getRecords().get(sheetName));
        sheetDataPreviewVO.setTotalRecord(excelPreviewInfoVO.getTotalCount().get(sheetName));
        return sheetDataPreviewVO;
    }

    @Override
    public ExcelPreviewInfoVO parseExcel(String fileMd5, String redisKey) {
        FileUploadInfo uploadFile = fileUploadInfoMapper.findByFileMd5(fileMd5);
        if (uploadFile == null) {
            return null;
        }

        ExcelPreviewInfoVO excelPreviewInfoVO = excelParseProcessor.doPreviewProcess(uploadFile.getFilePath());
        if (excelPreviewInfoVO == null) {
            return null;
        }
        redisUtil.setValueTimeout(redisKey, excelPreviewInfoVO, 15 * 60);
        return excelPreviewInfoVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean storeThirdPartyDatabaseData(String dataModelName, List<String> tableNameList, long userId, String userName) {
        // 从redis中获取本次查询的第三方数据库基本信息
        String databaseInfoRedisKey = DataCenterConstant.REDIS_THIRD_PARTY_DATABASE_PREFIX + dataModelName;
        DataWareHouseDatabaseInfo databaseInfo = (DataWareHouseDatabaseInfo) redisUtil.getValue(databaseInfoRedisKey);
        if (databaseInfo == null) {
            log.error("无法在redis缓存中获取数据仓库模块{}的基本信息", dataModelName);
            return false;
        }
        try {
            // 通过 Mybatis解决数据源信息 id 属性的来源问题
            dataWareHouseDatabaseInfoMapper.insertSelective(databaseInfo);
            Long id = databaseInfo.getId();
            log.debug("成功将第三方数据库信息插入数据库, 得到的id为{}", id);

            // 使用StringBuilder存储tableName
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : tableNameList) {
                stringBuilder.append(s);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            DataWareHouseInfo dataWareHouseInfo = new DataWareHouseInfo();
            dataWareHouseInfo.setDataModelName(dataModelName);
            dataWareHouseInfo.setFileId(0L);
            dataWareHouseInfo.setDatabaseId(id);
            dataWareHouseInfo.setDataSourceType(databaseInfo.getDatabaseType());
            dataWareHouseInfo.setGeneratorId(userId);
            dataWareHouseInfo.setGeneratorName(userName);
            dataWareHouseInfo.setTableList(stringBuilder.toString());
            dataWareHouseInfo.setIsUsed(1);
            dataWareHouseInfo.setIsStored(0);

            log.debug("存储的第三方数据库的信息{}", dataWareHouseInfo.toString());

            dataWareHouseInfoMapper.insertSelective(dataWareHouseInfo);

            // 执行异步任务
            boolean asyncTaskResult = asyncStoreThirdPartyData
                    .asyncStoreDatabase(databaseInfo, tableNameList, userId).get();
            if (asyncTaskResult) {
                dataWareHouseInfo.setIsStored(1);
                dataWareHouseInfoMapper.updateSelective(dataWareHouseInfo);
                return true;
            } else {
                redisUtil.delKey(databaseInfoRedisKey);
                dataWareHouseDatabaseInfoMapper.deleteRecordByDataModelName(dataModelName);
                dataWareHouseInfoMapper.deleteRecordByDataModelName(dataModelName);
                log.warn("{}数据源信息无法保存", dataModelName);
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            log.warn("{}数据源存储过程中发生了错误", dataModelName);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }


    @Override
    public boolean storeThirdPartyExcelData(String dataModelName, String fileMd5, List<String> sheetNameList,
                                            long userId, String userName) {
        FileUploadInfo uploadFile = fileUploadInfoMapper.findByFileMd5(fileMd5);
        if (uploadFile == null) {
            return false;
        }
        log.debug("保存的Excel文件路径为:{}, Sheet集合为:{}", uploadFile.getFilePath(), sheetNameList.toString());
        DataWareHouseFileInfo dataWareHouseFileInfo = new DataWareHouseFileInfo();
        dataWareHouseFileInfo.setDataModelName(dataModelName);
        dataWareHouseFileInfo.setFileId(uploadFile.getFileId());
        dataWareHouseFileInfo.setFileName(uploadFile.getFileName());
        dataWareHouseFileInfo.setFilePath(uploadFile.getFilePath());
        try {
            // 通过 Mybatis解决数据源信息 id 属性的来源问题
            dataWareHouseFileInfoMapper.insertSelective(dataWareHouseFileInfo);
            Long id = dataWareHouseFileInfo.getId();
            log.debug("成功将第三方文件信息信息插入数据库, 得到的id为{}", id);

            // 使用StringBuilder存储tableName
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : sheetNameList) {
                stringBuilder.append(s);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            DataWareHouseInfo dataWareHouseInfo = new DataWareHouseInfo();
            dataWareHouseInfo.setGeneratorId(userId);
            dataWareHouseInfo.setGeneratorName(userName);
            dataWareHouseInfo.setDataModelName(dataModelName);
            dataWareHouseInfo.setFileId(id);
            dataWareHouseInfo.setDatabaseId(0L);
            dataWareHouseInfo.setDataSourceType(uploadFile.getFileSuffix().toUpperCase());
            dataWareHouseInfo.setTableList(stringBuilder.toString());
            dataWareHouseInfo.setIsUsed(1);
            dataWareHouseInfo.setIsStored(0);

            dataWareHouseInfoMapper.insertSelective(dataWareHouseInfo);

            // 执行异步任务
            boolean asyncTaskResult = asyncStoreThirdPartyData
                    .asyncStoreExcel(dataWareHouseFileInfo, sheetNameList, userId).get();
            if (asyncTaskResult) {
                dataWareHouseInfo.setIsStored(1);
                dataWareHouseInfoMapper.updateSelective(dataWareHouseInfo);
                return true;
            } else {
                String redisKey = DataCenterConstant.REDIS_THIRD_PARTY_EXCEL_PREVIEW_INFO + uploadFile.getFileMd5();
                redisUtil.delKey(redisKey);
                dataWareHouseFileInfoMapper.deleteRecordByDataModelName(dataModelName);
                dataWareHouseInfoMapper.deleteRecordByDataModelName(dataModelName);
                log.warn("{}数据源信息无法保存", dataModelName);
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            log.warn("{}数据源存储过程中发生了错误", dataModelName);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

}

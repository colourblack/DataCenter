package com.yingf.controller;

import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.AjaxResult;
import com.yingf.domain.entity.DataWareHouseDatabaseInfo;
import com.yingf.domain.query.MultipartFileQuery;
import com.yingf.domain.query.SaveDatabaseQuery;
import com.yingf.domain.query.SaveExcelQuery;
import com.yingf.domain.vo.CheckFileUpLoadVO;
import com.yingf.domain.vo.PageResultVO;
import com.yingf.domain.vo.original.SheetDataPreviewVO;
import com.yingf.service.IDataCenterCommonService;
import com.yingf.service.IDataSourceGeneratorService;
import com.yingf.service.IFileTransferService;
import com.yingf.service.ITokenService;
import com.yingf.util.DataModelRedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 数据建模 - 生成数据源的相关接口
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 5:34 PM
 */
@Api(tags = "数据仓库 - 建立数据源模型")
@RestController
@RequestMapping("/data-center")
public class DataCenterGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(DataCenterGeneratorController.class);

    final IDataSourceGeneratorService dataWareHouseGeneratorServiceImpl;

    final IFileTransferService fileTransferServiceImpl;

    final IDataCenterCommonService dataCenterCommonServiceImpl;

    final ITokenService tokenServiceImpl;

    final DataModelRedisUtil redisUtil;


    @Autowired
    public DataCenterGeneratorController(IDataSourceGeneratorService dataWareHouseGeneratorServiceImpl,
                                         IFileTransferService fileTransferServiceImpl,
                                         IDataCenterCommonService dataCenterCommonServiceImpl, ITokenService tokenServiceImpl, DataModelRedisUtil redisUtil) {
        this.dataWareHouseGeneratorServiceImpl = dataWareHouseGeneratorServiceImpl;
        this.fileTransferServiceImpl = fileTransferServiceImpl;
        this.dataCenterCommonServiceImpl = dataCenterCommonServiceImpl;
        this.tokenServiceImpl = tokenServiceImpl;
        this.redisUtil = redisUtil;
    }


    @ApiOperation("校验数据模型的名称是否已经存在")
    @GetMapping("/get/check-name")
    public AjaxResult<String> checkDataModelName(@ApiParam("数据模型的名称") @RequestParam(value = "dataModelName") String dataModelName,
                                         HttpServletRequest request) {
        // 校验数据模型的名称是否合法
        if (dataModelName == null || (dataModelName.trim().length() == 0)) {
            return AjaxResult.success("数据模型的名称非法, 请检查数据模型名称");
        }
        long userId = tokenServiceImpl.getLoginUser(request).getSysAccountInfo().getUserId();
        log.debug("等待校验的数据模型名称为: {}, 登录的用户id为: {}", dataModelName, userId);
        if (!dataWareHouseGeneratorServiceImpl.hasDataModelName(dataModelName, userId)) {
            // 校验结果为false, 说明该名称未被使用
            return AjaxResult.success();
        } else {
            // 校验结果为true, 说明该名称已经使用
            return AjaxResult.success("该数据源名称已经存在");
        }
    }

    /**
     * 测试数据源连接与否并且保存成功的数据源配置信息
     *
     * @param databaseInfo 数据源的基本信息 --> ip, port, databaseName, username, password
     */
    @ApiOperation("第三方数据源为数据库 - 测试数据源连接与否并且保存成功的数据源配置信息")
    @PostMapping("/set/third-party-database")
    public AjaxResult<Boolean>  testAndSetDataSource(@RequestBody DataWareHouseDatabaseInfo databaseInfo, HttpServletRequest request) {
        if (checkDataModelNameUserId(request, databaseInfo.getDataModelName())) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }
        log.debug(databaseInfo.toString());

        boolean testResult = dataWareHouseGeneratorServiceImpl.testAndSetDatabaseCache(databaseInfo);

        if (testResult) {
            return AjaxResult.success("数据源: " + databaseInfo.getDatabaseName() + "连接成功. ", true);
        } else {
            return AjaxResult.success("数据源: " + databaseInfo.getDatabaseName() + "连接失败. ", false);
        }
    }

    /**
     * 分页查询查询数据源所有列表
     *
     * @param dataModelName 第三方数据库名称
     * @param currPage      页面当前页
     * @param pageSize      页面大小
     */
    @ApiOperation("第三方数据源为数据库 - 分页查询数据库所有表")
    @GetMapping("/get/db-tableList")
    public AjaxResult<PageResultVO> getDataBaseTableList(@ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                           @ApiParam("分页当前页") @RequestParam(value = "currPage") Integer currPage,
                                           @ApiParam("分页页大小") @RequestParam(value = "pageSize") Integer pageSize,
                                           HttpServletRequest request) {

        if (checkDataModelNameUserId(request, dataModelName)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }

        PageResultVO pageResultVO = dataWareHouseGeneratorServiceImpl.getDbTableNameList(dataModelName, currPage, pageSize, null);
        if (pageResultVO == null) {
            return AjaxResult.sysError("无法查询数据库的列表名 - " +  dataModelName);
        } else {
            return AjaxResult.success(pageResultVO);
        }
    }

    /**
     * 根据关键字搜索当前第三方数据源中所有列表名称
     *
     * @param dataModelName 当前数据建模模块名称
     * @param currPage      分页当前页
     * @param pageSize      分页页大小
     * @param keyword       关键字
     * @param request       HttpServletRequest
     * @return 返回模糊搜索结果
     */
    @ApiOperation("第三方数据源为数据库 - 根据关键字模糊搜索表")
    @GetMapping("/get/db-tableList-by-keyword")
    public AjaxResult<PageResultVO> getDataBaseTableListByKeyword(@ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                                    @ApiParam("分页当前页") @RequestParam(value = "currPage") Integer currPage,
                                                    @ApiParam("分页页大小") @RequestParam(value = "pageSize") Integer pageSize,
                                                    @ApiParam("模糊搜索关键字") @RequestParam(value = "keyword") String keyword,
                                                    HttpServletRequest request) {

        if (checkDataModelNameUserId(request, dataModelName)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return AjaxResult.success("模糊查询关键字不能为空, 请重新输入");
        }


        PageResultVO pageResultVO = dataWareHouseGeneratorServiceImpl.getDbTableNameList(dataModelName, currPage, pageSize, keyword);
        if (pageResultVO == null) {
            return AjaxResult.sysError("无法查询数据库的列表名 - " + dataModelName);
        } else {
            return AjaxResult.success(pageResultVO);
        }
    }


    /**
     * 查询所选择列表的预览信息
     * 其中包括:
     * 1. 前20条数据
     * 2. 该表格的count总数
     *
     * @param tableName     数据表表格名
     * @param dataModelName 当前数据模型名称
     */
    @ApiOperation("第三方数据源为数据库 - 预览选中表的数据")
    @GetMapping("/get/db-table-detail-preview")
    public AjaxResult<PageResultVO> showTableDetail(@ApiParam("数据表表名") @RequestParam(value = "tableName") String tableName,
                                      @ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                      HttpServletRequest request) {

        if (checkDataModelNameUserId(request, dataModelName)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }

        PageResultVO tablePreview = dataWareHouseGeneratorServiceImpl.getDbTableDetailPreview(dataModelName, tableName);
        if (tablePreview != null) {
            return AjaxResult.success(tablePreview);
        } else {
            return AjaxResult.sysError("出现错误! 无法查询选中列表的信息");
        }
    }

    /**
     * 将第三方数据库中选中的列表数据保存到本地的mongo db 中
     */
    @ApiOperation("第三方数据源为数据库 - 保存选中数据源数据")
    @PostMapping("/set/db-store-data")
    public AjaxResult<String> storeThirdPartyDatabaseData(@RequestBody SaveDatabaseQuery saveDatabaseQuery, HttpServletRequest request) {
        if (StringUtils.isEmpty(saveDatabaseQuery.getDataModelName()) || saveDatabaseQuery.getTableNameList().isEmpty()) {
            log.warn("请求参数有误, 请检擦参数");
            return AjaxResult.sysError("请求参数有误, 请检擦参数");
        }

        long userId = tokenServiceImpl.getLoginUser(request).getSysAccountInfo().getUserId();
        String username = tokenServiceImpl.getLoginUser(request).getUsername();
        String redisKey = DataCenterConstant.REDIS_DATA_WARE_HOUSE_NAME_PREFIX + saveDatabaseQuery.getDataModelName();
        if (redisUtil.getValue(redisKey) == null || (long) redisUtil.getValue(redisKey) != (userId)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");

        }

        if (dataWareHouseGeneratorServiceImpl.storeThirdPartyDatabaseData(saveDatabaseQuery.getDataModelName(),
                saveDatabaseQuery.getTableNameList(), userId, username)) {
            return AjaxResult.success();
        } else {
            return AjaxResult.sysError();
        }
    }


    /**
     * 若第三方数据源为文件时, 需要校验文件的状态
     * 通过对文件内容进行md5计算, 获取文件的信息
     *
     * @param param 封装的MultipartFileParam对象
     * @return CheckFileUpLoadVO
     */
    @ApiOperation("第三方数据源为文件 - 检验文件是否已经存在")
    @PostMapping("/set/third-party-file-check")
    public AjaxResult<CheckFileUpLoadVO> checkIsUploaded(MultipartFileQuery param) {
        return AjaxResult.success(fileTransferServiceImpl.findByFileMd5(param.getMd5()));
    }

    /**
     * 若第三方数据源为文件时, 完成文件状态校验以后, 执行上传操作
     * 通过控制上传状态来达到大文件分块上传的效果
     *
     * @param param         封装的MultipartFileParam对象
     * @param multipartFile Springboot封装http协议对于文件上传的参数对象, 通过这个参数可以获取文件的io流
     * @return CheckFileUpLoadVO
     */
    @ApiOperation("第三方数据源为文件 - 文件分片上传")
    @PostMapping("/set/third-party-file-upload")
    public AjaxResult<CheckFileUpLoadVO> uploadExcel(MultipartFileQuery param,
                                                     @RequestParam(value = "data", required = false) MultipartFile multipartFile) {
        CheckFileUpLoadVO vo = null;
        try {
            vo = fileTransferServiceImpl.doUpload(param, multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return AjaxResult.success(vo);
    }

    @ApiOperation("第三方数据源为Excel文件 - 解析Excel文件")
    @GetMapping("/get/excel-sheetList")
    public AjaxResult<PageResultVO> getExcelSheetList(@ApiParam("Excel文件的md5值") @RequestParam("fileMd5") String fileMd5,
                                        @ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                        @ApiParam("分页当前页") @RequestParam(value = "currPage") Integer currPage,
                                        @ApiParam("分页页大小") @RequestParam(value = "pageSize") Integer pageSize,
                                        HttpServletRequest request) {
        if (checkDataModelNameUserId(request, dataModelName)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }

        PageResultVO result = dataWareHouseGeneratorServiceImpl.getExcelSheetList(fileMd5, currPage, pageSize);
        if (result == null) {
            return AjaxResult.sysError("无法正确解析Excel文件信息, 请检查文件内容是否有误或重新上传");
        }
        return AjaxResult.success(result);

    }


    @ApiOperation("第三方数据源为Excel文件 - 获取指定sheet的预览信息")
    @GetMapping("/get/excel-sheet-preview")
    public AjaxResult<SheetDataPreviewVO> getExcelSheetDetailPreview(@ApiParam("Excel文件的md5值") @RequestParam(value = "fileMd5") String fileMd5,
                                                 @ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                                 @ApiParam("Excel Sheet名称") @RequestParam(value = "sheetName") String sheetName,
                                                 HttpServletRequest request) {
        if (checkDataModelNameUserId(request, dataModelName)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");
        }

        SheetDataPreviewVO vo = dataWareHouseGeneratorServiceImpl.getExcelSheetPreview(fileMd5, sheetName);
        if (vo == null) {
            return AjaxResult.sysError("无法正确获取sheet的预览信息, 请检查Sheet的名称是否合法, 或重新上传Excel");
        } else {
            return AjaxResult.success(vo);
        }
    }

    /**
     * 将第三方Excel中选中的所有sheet数据保存到本地的mongo db 中
     */
    @ApiOperation("第三方数据源为Excel - 保存选中sheet的数据")
    @PostMapping("/set/excel-store-data")
    public AjaxResult<String> storeThirdPartyExcelData(@RequestBody SaveExcelQuery saveExcelQuery, HttpServletRequest request) {
        String redisKey = DataCenterConstant.REDIS_DATA_WARE_HOUSE_NAME_PREFIX + saveExcelQuery.getDataModelName();
        long userId = tokenServiceImpl.getLoginUser(request).getSysAccountInfo().getUserId();
        if (redisUtil.getValue(redisKey) == null || (long) redisUtil.getValue(redisKey) != (userId)) {
            return AjaxResult.success("数据模型的名称与用户信息不匹配, 请重新创建数据模型");

        }
        String username = tokenServiceImpl.getLoginUser(request).getUsername();

        if (dataWareHouseGeneratorServiceImpl.storeThirdPartyExcelData(saveExcelQuery.getDataModelName(),
                saveExcelQuery.getFileMd5(), saveExcelQuery.getSheetNameList(), userId, username)) {
            return AjaxResult.success();
        } else {
            return AjaxResult.sysError();
        }
    }

    /**
     * 分页查询table所有字段和字段值
     *
     * @param tableName       数据表表格名
     * @param dataModelName   数据模块名称
     * @param currPage        页面当前页
     * @param pageSize        页面大小
     * @return 分页查询结果
     */
    @ApiOperation("分页查询数据表")
    @GetMapping("/get/table-detail")
    public AjaxResult<PageResultVO> getTableDetail(@ApiParam("数据表名称") @RequestParam(value = "tableName") String tableName,
                                     @ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                     @ApiParam("分页当前页")  @RequestParam(value = "currPage") Integer currPage,
                                     @ApiParam("分页页大小")  @RequestParam(value = "pageSize") Integer pageSize) {
        return AjaxResult.success(dataCenterCommonServiceImpl.getTableRecords(tableName, dataModelName, currPage, pageSize,
                DataCenterConstant.ORIGINAL_PARTITION));
    }


    /**
     * 数据处理首页信息
     * @param currPage 页面当前页
     * @param pageSize 页面大小
     * @return 首页信息
     */
    @ApiOperation("获取数据源信息")
    @GetMapping("/get/datasource-info")
    public AjaxResult<PageResultVO> getDataSourceInfo(@ApiParam("分页当前页") @RequestParam(value = "currPage") Integer currPage,
                                        @ApiParam("分页页大小") @RequestParam(value = "pageSize") Integer pageSize) {
        PageResultVO pageResult = dataCenterCommonServiceImpl.getDataSourceInfo(currPage, pageSize);
        log.debug("进入数据源模块, 读取数据源信息");
        return AjaxResult.success(pageResult);
    }

    /**
     * 在进行新的数据建模时, 需要判断当前用户信息和数据模块的名称是否匹配,
     * 因为在没有执行最后一个动作, 即本地存储第三方数据之前,
     * 所有的操作都不会进行持久化.
     *
     * @param request       HttpServletRequest, 用于获取用户信息
     * @param dataModelName 当前数据建模的模块名称
     * @return 用户信息是否与当前数据建模模块名称相匹配
     */
    private boolean checkDataModelNameUserId(HttpServletRequest request, String dataModelName) {
        long userId = tokenServiceImpl.getLoginUser(request).getSysAccountInfo().getUserId();
        String redisKey = DataCenterConstant.REDIS_DATA_WARE_HOUSE_NAME_PREFIX + dataModelName;
        return redisUtil.getValue(redisKey) == null || (long) redisUtil.getValue(redisKey) != (userId);
    }
}

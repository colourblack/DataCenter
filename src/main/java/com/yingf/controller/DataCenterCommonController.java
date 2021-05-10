package com.yingf.controller;

import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.AjaxResult;
import com.yingf.domain.vo.PageResultVO;
import com.yingf.domain.vo.original.TableDataTypeVO;
import com.yingf.service.IDataCenterCommonService;
import com.yingf.util.DataModelRedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 * @Description 数据仓库中心的通用controller
 * @Date 2021/3/15
 */
@RestController
@RequestMapping("/data-center")
@Api(tags = "数据仓库 - 通用接口")
public class DataCenterCommonController {

    private static final Logger log = LoggerFactory.getLogger(DataCenterCommonController.class);

    final IDataCenterCommonService dataCenterCommonServiceImpl;

    final DataModelRedisUtil dataModelRedisUtil;

    @Autowired
    public DataCenterCommonController(IDataCenterCommonService dataCenterCommonServiceImpl, DataModelRedisUtil dataModelRedisUtil) {
        this.dataCenterCommonServiceImpl = dataCenterCommonServiceImpl;
        this.dataModelRedisUtil = dataModelRedisUtil;
    }


    /**
     * 通过数据模块名称, 分区名称获取相应的数据表表名集合
     * @param dataModelName  指定的数据模块名称
     * @param currPage       分页当前页
     * @param pageSize       分页页大小
     * @param partitionType  分区名称
     * @return 查询的table name list集合
     */
    @ApiOperation("获取表名")
    @GetMapping("/get/table/list")
    public AjaxResult<List<String>> getOriginalTableName(@ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                           @ApiParam("分页当前页")  @RequestParam(value = "currPage") Integer currPage,
                                           @ApiParam("分页页大小")  @RequestParam(value = "pageSize") Integer pageSize,
                                           @ApiParam("数据表分区类型")  @RequestParam(value = "partitionType") String partitionType) {
        List<String> tableName = dataCenterCommonServiceImpl.getTableList(dataModelName, currPage, pageSize, partitionType);
        if (tableName == null || tableName.isEmpty()) {
            return AjaxResult.sysError();
        } else {
            return AjaxResult.success(tableName);
        }
    }

    /**
     * 分页查询数据表信息
     *
     * 注意: 这个方法中, 将分页结果重新转换成map, 因为要临时添加一个新的字段,
     * key:   status          - 状态
     * value: using / normal  - 该表正在被使用中 / 该表状态正常(无人使用)
     *
     * @param tableName       数据表表格名
     * @param dataModelName   数据源别名
     * @param currPage        页面当前页
     * @param pageSize        页面大小
     * @param partitionType   当前查询的数据表所属的分区类型
     * @return 分页查询结果
     */
    @ApiOperation("分页查询数据表信息")
    @GetMapping("/get/table/detail")
    public AjaxResult<Map<String, Object>> getTableDetail(@ApiParam("数据表名称") @RequestParam(value = "tableName") String tableName,
                                     @ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                     @ApiParam("分页当前页")  @RequestParam(value = "currPage") Integer currPage,
                                     @ApiParam("分页页大小")  @RequestParam(value = "pageSize") Integer pageSize,
                                     @ApiParam("数据表分区类型")  @RequestParam(value = "partitionType") String partitionType) {
        log.debug("查询[{}]仓库[{}]分区的[{}]表基本数据", dataModelName, partitionType, tableName);
        PageResultVO vo = dataCenterCommonServiceImpl
                .getTableRecords(tableName, dataModelName, currPage, pageSize, partitionType);

        if (vo != null) {
            // 若查询的分区为清洗分区, 则需要在redis中查看当前指定的数据表的状态
            // 因为清洗的表可能会在他人的清洗方案中, 换句话说该表此时的数据有可能是脏数据
            Map<String, Object> result = vo.convertMap();
            String s = "status";
            result.put(s, DataCenterConstant.TABLE_STATUS_NORMAL);
            if (DataCenterConstant.FILTER_PARTITION.equals(partitionType)) {
                String redisKey = DataCenterConstant.REDIS_TABLE_STATUS_PREFIX + partitionType + DataCenterConstant.MONGO_COLLECTION_SEP
                        + dataModelName + DataCenterConstant.MONGO_COLLECTION_SEP + tableName;
                if (dataModelRedisUtil.existsKey(redisKey)) {
                    result.put(s, DataCenterConstant.TABLE_STATUS_USING);
                }
            }
            return AjaxResult.success(result);
        } else {
            return AjaxResult.success("无法获取正确的数据表信息, 请检查请求参数是否合法");
        }
    }


    /**
     * 获取指定表的字段名和对应的字段类型
     *
     * @param dataModelName   数据源别名
     * @param tableName       数据表表格名
     * @param partitionType   当前查询的数据表所属的分区类型
     * @return TableDataTypeVO - Field name and Filed type
     */
    @ApiOperation("查询数据表的字段信息")
    @GetMapping("/get/table/struct")
    public AjaxResult<TableDataTypeVO> getTableFieldInfo(@ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                        @ApiParam("数据表名称") @RequestParam(value = "tableName") String tableName,
                                        @ApiParam("数据表分区类型")  @RequestParam(value = "partitionType") String partitionType) {
        TableDataTypeVO vo = dataCenterCommonServiceImpl.getTableDataType(dataModelName, tableName, partitionType);
        if (vo != null) {
            return AjaxResult.success(vo);
        } else {
            return AjaxResult.sysError("请求的参数有误, 请检查参数信息");
        }
    }

    /**
     * 查询当前数据所有模块的名称
     * @return list 所有数据模块名称
     */
    @ApiOperation("查询所有模块的名称")
    @GetMapping("/get/all-model-name")
    public AjaxResult<List<String>> getAllDataModelName() {
        return AjaxResult.success(dataCenterCommonServiceImpl.getAllModelName());
    }

}

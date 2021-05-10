package com.yingf.controller;

import com.yingf.constant.CleaningPlanConstant;
import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.AjaxResult;
import com.yingf.domain.dto.*;
import com.yingf.domain.entity.TableStruct;
import com.yingf.domain.query.clean.*;
import com.yingf.domain.vo.original.TableDataTypeVO;
import com.yingf.service.ICleaningTaskQueueService;
import com.yingf.service.IDataCenterCleaningService;
import com.yingf.util.DataModelRedisUtil;
import com.yingf.util.DataUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 10:33 AM
 */
@Api(tags = "数据中心 - 清洗分区")
@RestController
@RequestMapping("/data-center/cleaning")
public class DataCenterCleaningController {

    private final static Logger log = LoggerFactory.getLogger(DataCenterCleaningController.class);

    private final IDataCenterCleaningService dataCenterCleaningServiceImpl;

    private final ICleaningTaskQueueService cleaningTaskQueueServiceImpl;

    private final DataModelRedisUtil dataModelRedisUtil;

    public DataCenterCleaningController(IDataCenterCleaningService dataCenterCleaningServiceImpl,
                                        ICleaningTaskQueueService cleaningTaskQueueServiceImpl,
                                        DataModelRedisUtil dataModelRedisUtil) {
        this.dataCenterCleaningServiceImpl = dataCenterCleaningServiceImpl;
        this.cleaningTaskQueueServiceImpl = cleaningTaskQueueServiceImpl;
        this.dataModelRedisUtil = dataModelRedisUtil;
    }

    /**
     * 获取过滤日志信息
     *
     * @param dataModelName 数据源别名
     * @param tableName     选中的过滤表表名
     * @return 返回日志信息
     */
    @ApiOperation("获取过滤表日志信息")
    @GetMapping("/get/log-info")
    public AjaxResult<String> getFilterLogInfo(@ApiParam(value = "数据源名称") @RequestParam("dataModelName") String dataModelName,
                                               @ApiParam(value = "过滤表表名") @RequestParam("tableName") String tableName) {
        String res = dataCenterCleaningServiceImpl.getLogInfo(dataModelName, tableName);
        if (StringUtils.isEmpty(res)) {
            return AjaxResult.sysError("读取日志文件发送错误");
        } else {
            return AjaxResult.success(res);
        }
    }

    /**
     * 获取过滤表表头字段数据类型
     *
     * @param dataModelName 数据源别名
     * @param saveAs        过滤表表名(原名或者另存为名称)
     * @return 过滤表表头字段数据类型
     */
    @ApiOperation("获取数据表字段的数据类型")
    @GetMapping("/get/filed-info")
    public AjaxResult<TableDataTypeVO> getFilterTableDataType(@ApiParam("数据源名称") @RequestParam(value = "dataModelName") String dataModelName,
                                                              @ApiParam("过滤表表名") @RequestParam(value = "tableName") String tableName,
                                                              @ApiParam("过滤表另存为的表名称") @RequestParam(value = "saveAs") String saveAs) {
        if (StringUtils.isEmpty(dataModelName) || StringUtils.isEmpty(saveAs) || StringUtils.isEmpty(tableName)) {
            return AjaxResult.sysError("非法参数！");
        }

        TableDataTypeVO vo = dataCenterCleaningServiceImpl.getTableDataTypeFromRedis(dataModelName, tableName, saveAs);

        if (vo != null) {
            return AjaxResult.success(vo);
        } else {
            return AjaxResult.sysError("无法获取指定的数据表数据类型信息");
        }
    }


    /**
     * 获取已添加的过滤任务列表信息
     */
    @ApiOperation("获取已添加的过滤任务列表信息")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AbstractCleaningQuery")
    @PostMapping("/get/task")
    public AjaxResult<Map<String, Object>> getTask(@RequestBody AbstractCleaningQuery args) {
        String setKey = DataCenterConstant.USER_TASK_PREFIX + '-' + args.getUserId() + '-' + args.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + args.getTableName();
        Map<String, Object> allTask = new HashMap<>();
        Set<Object> userTask =  dataModelRedisUtil.getSetAll(setKey);
        Long size;
        for (Object o : userTask) {
            String str = (String) o;
            log.debug("redis key : {}", str);
            String[] s = DataUtil.splitStringOnFileSep( str);
            size = dataModelRedisUtil.listLength(str);
            allTask.put(s[2],dataModelRedisUtil.listGet(str, 0, size));
        }
        return AjaxResult.success(allTask);
    }


    /**
     * 进行空值处理操作
     */
    @ApiOperation("空值处理-填充或删除")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.NullValueHandlingQuery")
    @PostMapping("/set/do-null-filter")
    public AjaxResult<String> doNullProcessorFilter(@RequestBody NullValueHandlingQuery query) {
        log.debug("数据中心 - 清洗分区: 空值处理的请求参数, {}", query.toString());
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.NULL_CLEANING)) {
            return AjaxResult.sysError("请求空值处理时存在非法请求！");
        }
        NullValueHandlingDTO dto = new NullValueHandlingDTO();
        dto.setHandleType(CleaningPlanConstant.DO_NULL_CLEANING);
        BeanUtils.copyProperties(query, dto);
        log.debug("数据中心 - 清洗分区: 空值方案的DTO, {}", dto.toString());
        if (dataCenterCleaningServiceImpl.checkAvailableTask(dto)) {
            //todo 将任务添加到任务队列 用于定时任务使用
            cleaningTaskQueueServiceImpl.addNullProcessorArgs(dto);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(query));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }


    /**
     * 修改特定行
     */
    @ApiOperation("记录处理-修改指定记录")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterRowHandlingQuery")
    @PostMapping("/set/do-alter-row")
    public AjaxResult<String> doAlterSelectedRow(@RequestBody AlterRowHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_ROW)) {
            return AjaxResult.sysError("非法请求！");
        }
        AlterRowHandlingDTO dto = new AlterRowHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_ALTER_ROW);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(dto)) {
            cleaningTaskQueueServiceImpl.addAlterRowProcessorArgs(dto);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(query));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }

    /**
     * 修改特定行
     */
    @ApiOperation("记录处理-删除指定记录")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterRowHandlingQuery")
    @PostMapping("/set/do-delete-row")
    public AjaxResult<String> doDeleteSelectedRow(@RequestBody AlterRowHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_ROW)) {
            return AjaxResult.sysError("非法请求！");
        }
        AlterRowHandlingDTO dto = new AlterRowHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_DELETE_SELECTED_ROW);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(dto)) {
            cleaningTaskQueueServiceImpl.addDelRowProcessorArgs(dto);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(query));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }

    /**
     * 批量增加行
     */
    @ApiOperation("记录处理-批量新增记录")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterRowHandlingQuery")
    @PostMapping("/set/do-add-row")
    public AjaxResult<String> doAddRowBatch(@RequestBody AlterRowHandlingQuery query) {
        log.debug(query.toString());
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_ROW)) {
            return AjaxResult.sysError("非法请求！");
        }
        AlterRowHandlingDTO dto = new AlterRowHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_ADD_ROW_BATCH);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(dto)) {
            cleaningTaskQueueServiceImpl.addAddRowBatchProcessorArgs(dto);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(query));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }


    /**
     * 重复值处理
     */
    @ApiOperation("重复值处理-删除特定字段的重复值")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.DuplicateHandlingQuery")
    @PostMapping("/set/do-drop-duplicate")
    public AjaxResult<String> doDropDuplicates(@RequestBody DuplicateHandlingQuery query) {
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.DUPLICATE_CLEANING)) {
            return AjaxResult.sysError("非法请求！");
        }
        DuplicateHandlingDTO dto = new DuplicateHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_DUPLICATE_CLEANING);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(dto)) {
            cleaningTaskQueueServiceImpl.addDuplicateProcessorArgs(dto);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(query));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }

    /**
     * 批量增加列
     */
    @ApiOperation("字段处理-批量增加字段")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AddColHandlingQuery")
    @PostMapping("/set/do-add-col")
    public AjaxResult<String> doAddColBatch(@RequestBody AddColHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        boolean successModified = dataCenterCleaningServiceImpl.modifyTableStructWhenAddCol(query);
        if (successModified) {
            AddColHandlingDTO dto = new AddColHandlingDTO();
            BeanUtils.copyProperties(query, dto);
            dto.setHandleType(CleaningPlanConstant.DO_ADD_COL_BATCH);
            if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
                cleaningTaskQueueServiceImpl.addAddColBatch(query);
                return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
            }
            return AjaxResult.success(CleaningPlanConstant.FAILED);
        } else {
            return AjaxResult.sysError("清洗文件, 请删除此文件并重新开始进行过滤任务");
        }
    }


    /**
     * 校验指定字段的数据是否符合预期类型
     */
    @ApiOperation("字段处理-数据校验")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterColHandlingQuery")
    @PostMapping("/set/do-alter-col")
    public AjaxResult<String> doAlterCol(@RequestBody AlterColHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        // 获取缓存中的数据表结构信息, 并且更新字段名称
        TableStruct tableStruct = dataCenterCleaningServiceImpl.getSaveAsTableStruct(query.getDataModelName(),
                query.getSaveAs(), query.getTableName());
        tableStruct = dataCenterCleaningServiceImpl.renameField(tableStruct, query.getColArgs().getColName(), query.getColArgs().getRename());
        /*
         * 更新缓存中的数据表信息
         * - 当所有过滤任务执行成功以后
         * - python会将缓存中最新的数据表结构信息写入mongo db
         */
        dataModelRedisUtil.setValue(DataCenterConstant.REDIS_TABLE_STRUCT_PREFIX + DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.FILTER_PARTITION + query.getSaveAs(), tableStruct);
        AlterColHandlingDTO dto = new AlterColHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_ALTER_COL);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
            cleaningTaskQueueServiceImpl.addAlterColArgs(query);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }


    /**
     * 根据指定条件补全
     */
    @ApiOperation("字段处理-根据指定条件补全")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterColByConditionHandlingQuery")
    @PostMapping("/set/do-alter-col-cond")
    public AjaxResult<String> doAlterColCond(@RequestBody AlterColByConditionHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        AlterColByConditionHandlingDTO dto = new AlterColByConditionHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_ALTER_COL_BY_CONDITION);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
            cleaningTaskQueueServiceImpl.addAlterColByConditionArgs(query);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }

    /**
     * 删除指定表的字段
     */
    @ApiOperation("字段处理-删除指定表的字段")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.DelColArgsHandlingQuery")
    @PostMapping("/set/do-delete-col")
    public AjaxResult<String> doDeleteCol(@RequestBody DelColHandlingQuery query) {
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        boolean successModified = dataCenterCleaningServiceImpl.modifyTableStructWhenDelCol(query);
        if (successModified) {
            DelColHandlingDTO dto = new DelColHandlingDTO();
            BeanUtils.copyProperties(query, dto);
            dto.setHandleType(CleaningPlanConstant.DO_DELETE_COL);
            if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
                cleaningTaskQueueServiceImpl.addDelColArgs(query);
                return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
            }
            return AjaxResult.success(CleaningPlanConstant.FAILED);
        } else {
            return AjaxResult.sysError("过滤文件出错,请删除此文件并重新开始进行过滤任务");
        }
    }

    /**
     * 修改字段名以及字段顺序
     */
    @ApiOperation("字段处理-数据表字段重命名以及重新排序")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AlterTableFieldHandlingQuery")
    @PostMapping("/set/do-alter-table-field")
    public AjaxResult<String> doAlterField(@RequestBody AlterTableFieldHandlingQuery query) {
        // 对请求参数进行校验
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        // 在redis中更新table struct
        boolean successModified = dataCenterCleaningServiceImpl.modifyTableStructWhenAlterTableField(query);
        if (successModified) {
            AlterTableFieldHandlingDTO dto = new AlterTableFieldHandlingDTO();
            BeanUtils.copyProperties(query, dto);
            dto.setHandleType(CleaningPlanConstant.DO_ALTER_TABLE_FIELD);
            if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
                cleaningTaskQueueServiceImpl.addAlterTableFieldArgs(query);
                return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
            }
            return AjaxResult.success(CleaningPlanConstant.FAILED);
        } else {
            return AjaxResult.sysError("过滤文件出错,请删除此文件并重新开始进行过滤任务");
        }
    }

    @ApiOperation("字段处理-根据条件对字段的值进行筛选")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.FilterColByConditionQuery")
    @PostMapping("/set/filter-field-by-cond")
    public AjaxResult<String> filterColByCondition(@RequestBody FilterColByConditionQuery query) {
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.ALTER_COL)) {
            return AjaxResult.sysError("非法请求！");
        }
        FilterColByConditionDTO dto = new FilterColByConditionDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_FILTER_COL_BY_CONDITION);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
            cleaningTaskQueueServiceImpl.addFilterColByCondition(query);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }

    @ApiOperation("分组聚合运算")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.GroupByHandlingQuery")
    @PostMapping("/set/filter-group-by")
    public AjaxResult<String> filterGroupBy(@RequestBody GroupByHandlingQuery query) {
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !query.getProcessor().equals(CleaningPlanConstant.GROUP_BY)) {
            return AjaxResult.sysError("非法请求！");
        }
        GroupByHandlingDTO dto = new GroupByHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_GROUP_BY);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
            cleaningTaskQueueServiceImpl.addGroupByArgs(query);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }


    /**
     * 操作表建立与其他表的关联
     *
     * @param dataModelName    操作表数据源名称
     * @param saveAs           操作表表名
     * @param childSourceAlias 其他表（子表）数据源名称
     * @param childTableType   其他表表类型
     * @param childTableName   其他表表名
     * @return 是否成功建立关联
     */
    @ApiOperation("多表关联-建立关联关系")
    @GetMapping("/set/make-association")
    public AjaxResult<String> makeAssociation(@ApiParam("数据源名称") @RequestParam("dataModelName") String dataModelName,
                                              @ApiParam("过滤表另存为名称") @RequestParam("saveAs") String saveAs,
                                              @ApiParam("关联子表数据源名称") @RequestParam("childSourceAlias") String childSourceAlias,
                                              @ApiParam("关联子表类型") @RequestParam("childTableType") String childTableType,
                                              @ApiParam("关联子表表名") @RequestParam("childTableName") String childTableName) {
        // 若子表正在被使用中
        String dirtyTableKey = DataCenterConstant.REDIS_TABLE_STATUS_PREFIX + childTableType
                + DataCenterConstant.MONGO_COLLECTION_SEP + childSourceAlias
                + DataCenterConstant.MONGO_COLLECTION_SEP + childTableName;
        if (dataModelRedisUtil.existsKey(dirtyTableKey)) {
            return AjaxResult.success(DataCenterConstant.TABLE_STATUS_USING);
        }
        /* 需要用redis记录所有被关联的表名称, 在这里要查询childTableName是否被其他的表关联
         * todo 这里需要解决一个问题: 使用redis hash的过程中因为redis hash本身没有定时过期的功能, 因此要考虑脏数据内存驻留的问题
         *
         * 在redis中添加  hash name     ASSOCIATION_TABLE 用于存储关联表的表名以及该表被关联的次数
         *               hash key      Table Name(saveAs name)
         *               hash value    Child Table Name
         */
        String associationTableKey = DataCenterConstant.FILTER_PARTITION + DataCenterConstant.MONGO_COLLECTION_SEP + dataModelName
                + DataCenterConstant.MONGO_COLLECTION_SEP + saveAs;
        if (dataModelRedisUtil.hasHashKey(DataCenterConstant.ASSOCIATION_TABLE_HASH, associationTableKey)) {
            return AjaxResult.success("hasConn");
        }
        // 将关联子表加入到redis ASSOCIATION_TABLE中
        dataModelRedisUtil.hashPut(DataCenterConstant.ASSOCIATION_TABLE_HASH, associationTableKey, dirtyTableKey);
        return AjaxResult.success(DataCenterConstant.SUCCESS);
    }

    /**
     * 删除操作表的关联子表
     */
    @ApiOperation("多表关联-删除关联关系")
    @GetMapping("/get/delete-association")
    public AjaxResult<String> deleteAssociation(@ApiParam("数据源名称") @RequestParam("dataModelName") String dataModelName,
                                        @ApiParam("过滤表另存为名称") @RequestParam("saveAs") String saveAs) {
        dataModelRedisUtil.hashDeleteKey(DataCenterConstant.ASSOCIATION_TABLE_HASH, DataCenterConstant.FILTER_PARTITION
                + DataCenterConstant.MONGO_COLLECTION_SEP + dataModelName +DataCenterConstant.MONGO_COLLECTION_SEP + saveAs);
        return AjaxResult.success();
    }

    /**
     * 操作表与其子表进行合并操作
     */
    @ApiOperation("多表关联-连接两表")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.MergeTableHandlingQuery")
    @PostMapping("/set/merge-table")
    public AjaxResult<String> mergeTable(@RequestBody MergeTableHandlingQuery query) {
        if (StringUtils.isEmpty(query.getProcessor()) ||
                !CleaningPlanConstant.MERGE_TABLE.equals(query.getProcessor())) {
            return AjaxResult.sysError("非法请求！");
        }
        String associationTableKey = DataCenterConstant.FILTER_PARTITION + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getSaveAs();
        String childTableName = (String) dataModelRedisUtil.hashGet(DataCenterConstant.ASSOCIATION_TABLE_HASH, associationTableKey);
        if (StringUtils.isEmpty(childTableName)) {
            return AjaxResult.success("子表关联不存在，请重新建立关系");
        }
        // 判断连接字段是否相等
        if (!query.getLeftOn().equals(query.getRightOn())) {
            return AjaxResult.success(CleaningPlanConstant.FAILED);
        }
        // 更新缓存中数据表的结构信息
        boolean modifyTableStruct = dataCenterCleaningServiceImpl.modifyTableStructWhenMerge(query, childTableName);
        if (!modifyTableStruct) {
            return AjaxResult.success("子表关联不存在，请重新建立关系");
        }
        // 封装消息, 通过kafka向pandas发送
        MergeTableHandlingDTO dto = new MergeTableHandlingDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_MERGE_TABLE);
        dto.setAssociationTable(childTableName);
        if (dataCenterCleaningServiceImpl.checkAvailableTask(query)) {
            cleaningTaskQueueServiceImpl.addMergeTableArgs(query, childTableName);
            return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
        }
        return AjaxResult.success(CleaningPlanConstant.FAILED);
    }


    /**
     * 执行过滤方案并且保存
     */
    @ApiOperation("执行所有过滤任务")
    @PostMapping("/set/save-as")
    @ApiImplicitParam(dataType = "com.yingf.domain.query.clean.AbstractCleaningQuery")
    public AjaxResult<String> doFilterProcess(@RequestBody AbstractCleaningQuery query) {
        // 从redis中删除任务队列信息
        dataModelRedisUtil.delKey(DataCenterConstant.TASK_QUEUE_PREFIX + DataCenterConstant.FILTER_PARTITION + DataCenterConstant.MONGO_COLLECTION_SEP + query.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + query.getSaveAs());

        DoFilterProcessDTO dto = new DoFilterProcessDTO();
        BeanUtils.copyProperties(query, dto);
        dto.setHandleType(CleaningPlanConstant.DO_FILTER_PROCESS);
        return AjaxResult.success(dataCenterCleaningServiceImpl.sendTaskMessage(dto));
    }

}

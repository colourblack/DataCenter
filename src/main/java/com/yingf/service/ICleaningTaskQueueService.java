package com.yingf.service;

import com.yingf.domain.query.clean.*;

/**
 * 将任务信息翻译成简要内容添加到缓存, 便于前端获取信息列表
 *
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 3:14 PM
 */

public interface ICleaningTaskQueueService {
    /**
     * 任务信息: 空值处理
     *
     * @param args 参数
     */
    void addNullProcessorArgs(NullValueHandlingQuery args);

    /**
     * 任务信息: 重复值处理
     *
     * @param args 参数
     */
    void addDuplicateProcessorArgs(DuplicateHandlingQuery args);

    /**
     * 任务信息: 记录处理-修改指定行
     *
     * @param args 参数
     */
    void addAlterRowProcessorArgs(AlterRowHandlingQuery args);

    /**
     * 任务信息: 记录处理-删除增行
     *
     * @param args 参数
     */
    void addDelRowProcessorArgs(AlterRowHandlingQuery args);

    /**
     * 任务信息: 记录处理-批量新增行
     *
     * @param args 参数
     */
    void addAddRowBatchProcessorArgs(AlterRowHandlingQuery args);

    /**
     * 任务信息: 字段处理-批量删除字段
     *
     * @param args 参数
     */

    void addDelColArgs(DelColHandlingQuery args);

    /**
     * 任务信息: 字段处理-批量新增字段
     *
     * @param args 参数
     */
    void addAddColBatch(AddColHandlingQuery args);

    /**
     * 任务信息: 字段处理-根据条件修改字段的值
     *
     * @param args 参数
     */
    void addAlterColByConditionArgs(AlterColByConditionHandlingQuery args);

    /**
     * 任务信息: 字段处理-字段数据校验
     *
     * @param args 参数
     */
    void addAlterColArgs(AlterColHandlingQuery args);

    /**
     * 任务信息: 字段处理-表格字段重命名以及重新排序
     *
     * @param args 参数
     */
    void addAlterTableFieldArgs(AlterTableFieldHandlingQuery args);

    /**
     * 任务信息: 字段处理-根据条件筛选字段值
     *
     * @param args 参数
     */
    void addFilterColByCondition(FilterColByConditionQuery args);

    /**
     * 任务信息: 多表关联
     *
     * @param args            参数
     * @param childTableName  关联子表表名
     */
    void addMergeTableArgs(MergeTableHandlingQuery args, String childTableName);

    /**
     * 任务信息: 分组聚合运算
     *
     * @param args 参数
     */
    void addGroupByArgs(GroupByHandlingQuery args);
}

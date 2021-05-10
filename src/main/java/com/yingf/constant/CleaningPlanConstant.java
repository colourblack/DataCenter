package com.yingf.constant;

/**
 * @author yingf Fangjunjin
 * @Description 数据中心 - 清洗方案
 * @Date 2021/3/16
 */
public class CleaningPlanConstant {

    /** 任务 - 失败 */
    public static final String FAILED = "failed";
    /** 任务 - 成功 */
    public static final String SUCCESS = "success";

    /** 数据清洗 - 下达执行任务的指令*/
    public static final String DO_FILTER_PROCESS = "doFilterProcess";

    /** 数据清洗 - 空值处理 */
    public static final String NULL_CLEANING = "nullProcessor";
    public static final String DO_NULL_CLEANING = "doNullProcess";

    /** 数据清洗 - 记录处理*/
    public static final String ALTER_ROW = "alterRowProcessor";
    public static final String DO_ADD_ROW_BATCH = "doAddRowBatch";
    public static final String DO_ALTER_ROW = "doAlterRow";
    public static final String DO_DELETE_SELECTED_ROW = "doDeleteSelectedRow";

    /** 数据清洗 - 字段处理*/
    public static final String ALTER_COL = "alterColProcessor";
    public static final String DO_ADD_COL_BATCH = " doAddColBatch";
    public static final String DO_ALTER_COL = "doAlterCol";
    public static final String DO_DELETE_COL = "doDeleteCol";
    public static final String DO_ALTER_COL_BY_CONDITION = "doAlterColByCondition";
    public static final String DO_ALTER_TABLE_FIELD = "doAlterTableField";
    public static final String DO_FILTER_COL_BY_CONDITION = "doFilterColByCondition";


    /** 数据清洗 - 多表关联操作处理 */
    public static final String MERGE_TABLE = "mergeProcessor";
    public static final String DO_MERGE_TABLE = "doMergeTable";

    /** 数据清洗 - 重复值处理 */
    public static final String DUPLICATE_CLEANING = "duplicateProcessor";
    public static final String DO_DUPLICATE_CLEANING = "doDuplicate";

    /** 数据清洗 - 分组聚合运算 */
    public static final String GROUP_BY =  "groupByProcessor";
    public static final String DO_GROUP_BY =  "doGroupBy";

}

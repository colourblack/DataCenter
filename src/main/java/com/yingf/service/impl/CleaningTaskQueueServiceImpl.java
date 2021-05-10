package com.yingf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yingf.constant.DataCenterConstant;
import com.yingf.domain.filter.FilterTaskArgs;
import com.yingf.domain.query.clean.*;
import com.yingf.service.ICleaningTaskQueueService;
import com.yingf.util.DataModelRedisUtil;
import com.yingf.util.DataUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/8/21 3:16 PM
 */
@Service
public class CleaningTaskQueueServiceImpl implements ICleaningTaskQueueService {

    private final SimpleDateFormat formatter;

    private final DataModelRedisUtil dataModelRedisUtil;

    public CleaningTaskQueueServiceImpl(DataModelRedisUtil dataModelRedisUtil) {
        this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dataModelRedisUtil = dataModelRedisUtil;
    }

    @Override
    public void addNullProcessorArgs(NullValueHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "空值处理";
        String content;
        Map<String, Object> hidden = new HashMap<>();
        if (args.getCol().size() == 0) {
            hidden.put("选中字段", "所有");
        } else {
            hidden.put("选中字段", args.getCol());
        }
        if (args.getNullProcessType().equals("del")) {
            content = "删除选中字段的空值所在记录";
        } else {
            content = "填充选中字段的所有空值";
            hidden.put("填充值", args.getFillValue());
            if (args.getRow().size() == 0) {
                hidden.put("影响记录总数", "所有");
            } else {
                hidden.put("影响记录位置", args.getRow());
            }
        }
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }


    @Override
    public void addAddRowBatchProcessorArgs(AlterRowHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "记录处理";
        String content = "过滤表新增记录";
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("新增记录数", args.getRowBatch().size());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addAlterRowProcessorArgs(AlterRowHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "记录处理";
        String content = "修改指定记录的内容";
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("选中记录的位置", args.getRowData().keySet());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addDelRowProcessorArgs(AlterRowHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "记录处理";
        String content = "过滤表删除选中记录";
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("选中记录总数", args.getRow());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addDuplicateProcessorArgs(DuplicateHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "重复值处理";
        String content = "删除选中字段的重复值以及该值所在的记录";
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("选中字段", args.getColName());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addAddColBatch(AddColHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段处理";
        String content = "批量新增指定的字段";
        Map<String, Object> hidden = new HashMap<>();
        int size = args.getColData().size();
        for (int i = 1; i <= size; i++) {
            hidden.put("字段信息" + i, args.getColData().get(i - 1).toString());
        }
        dataModelRedisUtil.listRightPush(redisKey, new FilterTaskArgs(header, content, dateTime, hidden));
    }

    @Override
    public void addDelColArgs(DelColHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段处理";
        String content = "批量删除指定的字段";
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("字段名", args.getColData());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addAlterColArgs(AlterColHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段处理";
        String content = "数据校验";
        Map<String, Object> hidden = new HashMap<>();
        AlterColHandlingQuery.CalibrationParam colData = args.getColArgs();
        hidden.put("字段名", colData.getColName());
        hidden.put("重命名名称", colData.getRename());
        switch (colData.getColType()) {
            case "int64":
                hidden.put("字段类型", "整型");
                break;
            case "float64":
                hidden.put("字段类型", "浮点型");
                break;
            case "object":
                hidden.put("字段类型", "字符串");
                break;
            case "bool":
                hidden.put("字段类型", "布尔");
                break;
            case "datetime64":
                hidden.put("字段类型", "日期时间");
                break;
            default:
                hidden.put("字段类型", "");
                break;
        }
        switch (colData.getInvalidValHandler()) {
            case "fill":
                hidden.put("填充值", colData.getFillVal());
                hidden.put("非法值处理方式", "填充");
                break;
            case "empty":
                hidden.put("非法值处理方式", "清空");
                break;
            case "defaultHandle":
                hidden.put("非法值处理方式", "默认处理");
                break;
            default:
                hidden.put("非法值处理方式", "");
                break;
        }
        switch (colData.getContentType()) {
            case "%Y-%m-%d %H:%M:%S":
                hidden.put("填充值格式", "日期时间");
                break;
            case "%Y-%m-%d":
                hidden.put("填充值格式", "年月日");
                break;
            case "%Y-%m":
                hidden.put("填充值格式", "年月");
                break;
            case "%Y":
                hidden.put("填充值格式", "年");
                break;
            case "week":
                hidden.put("填充值格式", "周");
                break;
            case "number":
                hidden.put("填充值格式", "True / False");
                break;
            case "char":
                hidden.put("填充值格式", "1 / 0");
                break;
            case "keepChineseNumber":
                hidden.put("填充值格式", "保留中文数字");
                break;
            case "keepEnglishNumber":
                hidden.put("填充值格式", "保留英文数字");
                break;
            case "removeNumber":
                hidden.put("填充值格式", "去除数字");
                break;
            case "removeSpecialSymbol":
                hidden.put("填充值格式", "去除特殊字符");
                break;
            case "object":
                hidden.put("填充值格式", "字符串");
                break;
            default:
                hidden.put("填充值格式", "");
                break;
        }
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }


    @Override
    public void addAlterColByConditionArgs(AlterColByConditionHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段处理";
        String content = "根据条件对指定字段数据进行修改";
        Map<String, Object> hidden = new HashMap<>();
        AddColHandlingQuery.ColStruct colData = args.getColData();
        hidden.put("指定字段名", colData.getColName());
        hidden.put("指定字段类型", colData.getColType());
        hidden.put("指定字段值", colData.getDefaultVal());
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addAlterTableFieldArgs(AlterTableFieldHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段处理";
        String content = "表格字段重新排序和重命名";
        // todo hidden的值
        Map<String, Object> hidden = new HashMap<>();
        dataModelRedisUtil.listRightPush(redisKey, new FilterTaskArgs(header, content, dateTime, hidden));
    }

    @Override
    public void addFilterColByCondition(FilterColByConditionQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "字段";
        String content = "根据条件筛选字段值";
        // todo hidden的值
        Map<String, Object> hidden = new HashMap<>();
        dataModelRedisUtil.listRightPush(redisKey, new FilterTaskArgs(header, content, dateTime, hidden));
    }


    @Override
    public void addMergeTableArgs(MergeTableHandlingQuery args, String childTableName) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "多表关联";
        String content = "将过滤表与选中的子表通过指定字段和关联方式进行关联";
        Map<String, Object> hidden = new HashMap<>();
        String[] childNameArr = DataUtil.splitStringOnFileSep(childTableName);
        hidden.put("关联表数据源", childNameArr[1]);
        if (childNameArr[0].equals(DataCenterConstant.ORIGINAL_PARTITION)) {
            hidden.put("关联表表类型", "原始表");
        }
        if (childNameArr[0].equals(DataCenterConstant.FILTER_PARTITION)) {
            hidden.put("关联表表类型", "过滤表");
        }
        hidden.put("关联表表名", childNameArr[2]);
        switch (args.getHow()) {
            case "left":
                hidden.put("关联方式", "左连接");
                break;
            case "right":
                hidden.put("关联方式", "右连接");
                break;
            case "inner":
                hidden.put("关联方式", "内连接");
                break;
            case "outer":
                hidden.put("关联方式", "外连接");
                break;
            default:
                hidden.put("关联方式", "非法连接方式");
        }
        if ("index".equals(args.getConnType())) {
            hidden.put("关联类型", "索引连接");
        } else {
            hidden.put("关联类型", "字段连接");
        }

        hidden.put("父表字段", Arrays.toString(args.getLeftOn().toArray()));
        hidden.put("子表字段", Arrays.toString(args.getRightOn().toArray()));
        JSONObject json = (JSONObject) JSON.toJSON(new FilterTaskArgs(header, content, dateTime, hidden));
        dataModelRedisUtil.listRightPush(redisKey, json);
    }

    @Override
    public void addGroupByArgs(GroupByHandlingQuery args) {
        putUserTaskIntoQueue(args);
        String redisKey = getRedisKey(args);
        Date date = new Date();
        String dateTime = this.formatter.format(date);
        String header = "聚合运算";
        String content = "分组聚合运算";
        // todo hidden的值
        Map<String, Object> hidden = new HashMap<>();
        dataModelRedisUtil.listRightPush(redisKey, new FilterTaskArgs(header, content, dateTime, hidden));
    }

    private void putUserTaskIntoQueue(AbstractCleaningQuery args) {
        String redisSetKey = DataCenterConstant.USER_TASK_PREFIX + '-' + args.getUserId() + '-' + args.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + args.getTableName();
        String redisSetValue = getRedisKey(args);
        dataModelRedisUtil.setSet(redisSetKey, redisSetValue);
    }


    private String getRedisKey(AbstractCleaningQuery args) {
        return DataCenterConstant.TASK_QUEUE_PREFIX + DataCenterConstant.FILTER_PARTITION + DataCenterConstant.MONGO_COLLECTION_SEP + args.getDataModelName()
                + DataCenterConstant.MONGO_COLLECTION_SEP + args.getSaveAs();
    }
}

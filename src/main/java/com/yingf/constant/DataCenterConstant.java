package com.yingf.constant;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author yingf Fangjunjin
 * @Description 数据仓库 - 常量字符串
 * @Date 2021/3/3
 */
@Component
public class DataCenterConstant {

    static String uploadExcelPath;

    static {
        File upload = new File(System.getProperty("user.dir"), "static/data-model/upload/excel");
        if (!upload.exists()) {
            upload.mkdirs();
        }
        uploadExcelPath = upload.getAbsolutePath();
    }

    /**
     * 数据仓库 - 抽取第三方数据源进行建模过程中, Excel文件上传路径配置
     */
    public final static String UPLOAD_EXCEL_FILE_PATH = uploadExcelPath;

    /* ---------------------   Mongo db    ---------------------- */

    /**
     * Mongodb中集合的分割符
     */
    public final static String MONGO_COLLECTION_SEP = ".";

    public final static String ORIGINAL_PARTITION = "original";

    public final static String FILTER_PARTITION = "filter";

    public final static String PIVOT_PARTITION = "pivot";

    public final static String TABLE_STRUCT_SUFFIX = "struct";
    public final static String LOG_STRUCT_SUFFIX = "struct";

    /* ---------------------   kafka topic    ---------------------- */

    public final static String STORE_TASK_CHANNEL = "StoreFile";
    public final static String FILTER_TASK_CHANNEL = "FilterTask";

    /**
     * 通知kafka生成数据表结构信息的请求参数 req
     */
    public final static String TABLE_STRUCT_REQ = "generateTableStruct";


    /**
     * 通知python生成数据表结构信息的 kafka topic
     */
    public final static String TABLE_STRUCT_TOPIC = "DATA_MODEL_FILTER_TASK";


    /* ---------------------   Redis Key   ---------------------- */

    /**
     * redis key 前缀: 数据仓库(建模) - 抽取第三方数据源进行建模, 数据模型名称
     */
    public final static String REDIS_DATA_WARE_HOUSE_NAME_PREFIX = "DATA_WARE_HOUSE_NAME-";

    /**
     * redis key前缀: 数据仓库(建模) - 抽取第三方数据源进行建模, 第三方数据源的配置信息
     */
    public final static String REDIS_THIRD_PARTY_DATABASE_PREFIX = "THIRD_PARTY_DATABASE_INFO-";

    /**
     * redis key前缀: 数据仓库(建模) - 抽取第三方数据源进行建模, 指定第三方数据源的TableNameList
     */
    public final static String REDIS_THIRD_PARTY_DATABASE_TABLE_LIST = "THIRD_PARTY_DATABASE_TABLE_LIST-";


    /**
     * redis key前缀: 数据仓库(建模) - 当第三方数据源为Excel时, 获取指定Excel的预览信息
     */
    public final static String REDIS_THIRD_PARTY_EXCEL_PREVIEW_INFO = "THIRD_PARTY_EXCEL_PREVIEW_INFO-";

    /**
     * redis key前缀: 数据仓库(清洗分区) - 查看redis中指定表的表结构信息
     */
    public final static String REDIS_TABLE_STRUCT_PREFIX = "REDIS_TABLE_STRUCT_INFO-";

    /**
     * redis key前缀: 数据仓库(清洗分区) - 查看选中清洗分区的表的状态
     * 状态: 1.correct
     *      2.writing
     *      3.dirty
     */
    public final static String REDIS_TABLE_STATUS_PREFIX = "TABLE_STATUS-";
    public final static String TABLE_STATUS_NORMAL = "normal";
    public final static String TABLE_STATUS_DIRTY = "dirty";


    /**
     *  redis list key前缀: 数据仓库(清洗分区) - 用于存储指定表的关联子表
     */
    public final static String REDIS_ASSOCIATION_TABLE_LIST_PREFIX = "ASSOCIATION_TABLE_LIST-";
}

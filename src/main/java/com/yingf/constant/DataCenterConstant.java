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

    public final static String SUCCESS = "success";
    public final static String FAILED = "failed";

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
    public final static String LOG_STRUCT_SUFFIX = "log ";

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
     */
    public final static String REDIS_TABLE_STATUS_PREFIX = "TABLE_STATUS-";

    /** table status 数据表的状态 - normal  / using */
    public final static String TABLE_STATUS_NORMAL = "normal";
    public final static String TABLE_STATUS_USING = "using";


    /** Redis Hash:   hash name    ASSOCIATION_TABLE = DataModel-AssociationTableList
     *               hash key     association-table name: table type + datasourceAlias + table name
     *               hash value   the number of table which has been associated with association-table
     *  todo 没有进行定时处理该hash, 因此需要考虑内存驻留问题*/
    public final static String ASSOCIATION_TABLE_HASH = "DATA_MODEL_ASSOCIATION_TABLE_HASH-";

    /** redis list : 用于记录清洗队列
     *  todo 没有进行定时处理该list, 因此需要考虑内存驻留问题*/
    public final static String TASK_QUEUE_PREFIX = "DATA_MODEL_TASK_QUEUE-";
    /** redis set  : 用户所拥有的所有的清洗队列
     *  todo 没有进行定时处理该set, 因此需要考虑内存驻留问题*/
    public final static String USER_TASK_PREFIX = "USER_TASK_SET-";

}

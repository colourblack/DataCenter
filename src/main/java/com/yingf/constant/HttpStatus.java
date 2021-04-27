package com.yingf.constant;

/**
 * 返回状态码
 *
 * @author ruoyi
 */
public class HttpStatus
{
    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;

    /** 操作失败 */
    public static final int FAILED = 400;
    /**
     * 未授权
     */
    public static final int AUTH_ERROR = 401;
    /**
     * 资源，服务未找到
     */
    public static final int NOT_FOUND = 404;
    /**
     * 系统内部错误
     */
    public static final int SYSTEM_ERROR = 500;


    /** 其他服务错误 */
    public static final int RPC_ERROR = 503;
}

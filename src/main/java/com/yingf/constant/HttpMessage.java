package com.yingf.constant;

/**
 * Http 请求常用返回值
 *
 * @author yingf-Fangjunjin
 * @version 1.0
 * @since 2021/3/23 上午10:42
 */
public class HttpMessage {

    /** 操作成功 */
    public final static String SUCCESS = "操作成功";

    /** 操作失败 */
    public final static String FAILED = "操作失败";

    /** 未经过身份认证 */
    public final static String AUTH_ERROR = "未经过身份认证";

    /** Token非法, 无访问权限 */
    public final static String NO_TOKEN = "Token非法, 无访问权限";

    /** 请求校验失败 */
    public final static String CHECK_SECRET = "请求校验失败";

    /** 资源不存在 */
    public final static String NOT_FOUND = "资源不存在";

    /** 服务器异常,请稍后再试 */
    public final static String SYSTEM_ERROR = "服务器异常, 请稍后再试";

    /** 其他服务错误 */
    public final static String RPC_ERROR = "外部服务异常";

}

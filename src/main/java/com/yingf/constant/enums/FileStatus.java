package com.yingf.constant.enums;

/**
 * @author yingf Fangjunjin
 * @Description 服务器文件状态
 * @Date 2021/3/10
 */
public enum  FileStatus {

    /**
     * 已经存在
     */
    EXIST,

    /**
     * 已经不存在
     */
    NOT_EXIST,

    /**
     * 上传了部分, 不完全
     */
    INCOMPLETE
}

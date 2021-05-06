package com.yingf.constant.enums;

/**
 * @author yingf Fangjunjin
 * @Description 文件上传过程中, 文件的状态
 * @Date 2021/3/10
 */
public enum UploadFileActionType {

    /**
     * 当前操作(action of doUpload)为check, 校验分片是否上传过
     */
    CHECK,

    /**
     * 当前操作(action of doUpload)为upload, 直接上传分片
     */
    UPLOAD,


}

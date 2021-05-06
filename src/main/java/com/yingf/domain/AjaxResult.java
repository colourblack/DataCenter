package com.yingf.domain;

import com.yingf.constant.HttpMessage;
import com.yingf.constant.HttpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 *
 *
 * @author yingf-Fangjunjin
 * @version 1.0
 * @since 2021/3/23 上午9:47
 */
@ApiModel("统一API响应结果封装")
public class AjaxResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应状态码 */
    @ApiModelProperty(value = "成功失败的标志",required = true)
    private int code;

    /** 响应信息，用来说明响应情况 */
    @ApiModelProperty(value = "成功失败的响应信息",required = true)
    private String msg;

    /** 响应的具体数据 */
    @ApiModelProperty(value = "成功失败的响应数据",required = false)
    private T data;


    public AjaxResult(T data) {
        this.data = data;
    }

    public AjaxResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public AjaxResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success() {
        return new AjaxResult<>(HttpStatus.SUCCESS, HttpMessage.SUCCESS);
    }

    public static <T> AjaxResult<T> success(String message) {return new AjaxResult<>(HttpStatus.SUCCESS, message);}


    /**
     * 返回成功消息
     *
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(HttpStatus.SUCCESS, HttpMessage.SUCCESS, data);
    }

    public static <T> AjaxResult<T> success(String message, T data) {
        return new AjaxResult<>(HttpStatus.SUCCESS, message, data);
    }

    /**
     * 操作失败
     *
     * @return 警告消息
     */
    public static <T> AjaxResult<T> failed() {
        return new AjaxResult<>(HttpStatus.FAILED, HttpMessage.FAILED);
    }

    /**
     * 操作失败
     *
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> AjaxResult<T> failed(T data) {
        return new AjaxResult<>(HttpStatus.FAILED, HttpMessage.FAILED, data);
    }

    /**
     * 服务器异常
     *
     * @return 服务器错误消息
     */
    public static <T> AjaxResult<T> sysError() {
        return new AjaxResult<>(HttpStatus.SYSTEM_ERROR, HttpMessage.SYSTEM_ERROR);
    }

    public static <T> AjaxResult<T> sysError(String message) {
        return new AjaxResult<>(HttpStatus.SYSTEM_ERROR, message);
    }

    /**
     * 服务器异常
     *
     * @param data 数据对象
     * @return 服务器错误消息
     */
    public static <T> AjaxResult<T> sysError(T data) {
        return new AjaxResult<>(HttpStatus.SYSTEM_ERROR, HttpMessage.SYSTEM_ERROR, data);
    }

    public static <T> AjaxResult<T> sysError(String message, T data) {
        return new AjaxResult<>(HttpStatus.SYSTEM_ERROR, message, data);
    }

    /**
     * 未经过身份认证
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> authError(){
        return new AjaxResult<>(HttpStatus.AUTH_ERROR, HttpMessage.AUTH_ERROR);
    }

    /**
     * 未经过身份认证
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> authError(T data){
        return new AjaxResult<>(HttpStatus.AUTH_ERROR, HttpMessage.AUTH_ERROR, data);
    }

    /**
     * 资源不存在
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> notFoundError(){
        return new AjaxResult<T>(HttpStatus.NOT_FOUND, HttpMessage.NOT_FOUND);
    }

    /**
     * 资源不存在
     *
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> notFoundError(T data) {
        return new AjaxResult<T>(HttpStatus.NOT_FOUND, HttpMessage.NOT_FOUND, data);
    }


    /**
     * 其他服务错误
     *
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> rpcError() {
        return new AjaxResult<T>(HttpStatus.RPC_ERROR, HttpMessage.RPC_ERROR);
    }

    /**
     * 其他服务错误
     *
     * @param <T> 返回值类型
     * @return 警告消息
     */
    public static <T> AjaxResult<T> rpcError(T data) {
        return new AjaxResult<T>(HttpStatus.RPC_ERROR, HttpMessage.RPC_ERROR, data);
    }

    @Override
    public String toString() {
        return "AjaxResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data.toString() +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

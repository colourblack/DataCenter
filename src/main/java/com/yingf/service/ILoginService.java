package com.yingf.service;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 1:58 PM
 */
public interface ILoginService {

    /**
     * 校验登陆是否为合法登陆
     * @param accountName 用户名
     * @param password    密码
     * @return token
     */
    String login(String accountName, String password);
}

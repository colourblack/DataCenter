package com.yingf.service;

import com.yingf.domain.LoginUserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * token验证处理
 *
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 9:52 AM
 */
public interface ITokenService {


    /**
     * 获取用户身份信息
     * @param request http request
     * @return 用户信息
     */
    LoginUserDetails getLoginUser(HttpServletRequest request);


    /**
     * 设置用户身份信息
     */
    void setLoginUser(LoginUserDetails loginUser);

    /**
     * 删除用户身份信息
     *  @param token string token
     */
    void delLoginUser(String token);

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    String createToken(LoginUserDetails loginUser);

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 令牌
     */
    void verifyToken(LoginUserDetails loginUser);

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    void refreshToken(LoginUserDetails loginUser);


}

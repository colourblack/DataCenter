package com.yingf.component;

import com.alibaba.fastjson.JSON;
import com.yingf.domain.AjaxResult;
import com.yingf.domain.LoginUserDetails;
import com.yingf.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 11:10 AM
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final ITokenService tokenServiceImpl;

    @Autowired
    public LogoutSuccessHandlerImpl(ITokenService tokenServiceImpl) {
        this.tokenServiceImpl = tokenServiceImpl;
    }

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        LoginUserDetails loginUser = tokenServiceImpl.getLoginUser(request);
        if (loginUser != null) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenServiceImpl.delLoginUser(loginUser.getToken());
        }
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(AjaxResult.sysError("退出成功")));
    }
}
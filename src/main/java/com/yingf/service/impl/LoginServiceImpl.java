package com.yingf.service.impl;

import com.yingf.domain.LoginUserDetails;
import com.yingf.service.ILoginService;
import com.yingf.service.ITokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 2:00 PM
 */
@Service
public class LoginServiceImpl implements ILoginService {

    private final static Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final AuthenticationManager authenticationManager;

    private final ITokenService tokenServiceImpl;

    @Autowired
    public LoginServiceImpl(AuthenticationManager authenticationManager, ITokenService tokenServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    public String login(String accountName, String password) {
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(accountName, password));
        }
        catch (Exception e) {
            log.error("登陆失败!, {}", e.getMessage());
        }
        LoginUserDetails loginUser = (LoginUserDetails) authentication.getPrincipal();
        // 生成token
        return tokenServiceImpl.createToken(loginUser);
    }
}

package com.yingf.controller;

import com.yingf.domain.AjaxResult;
import com.yingf.domain.vo.LoginVO;
import com.yingf.domain.vo.TokenVO;
import com.yingf.service.ILoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 4:22 PM
 */
@RestController
public class LoginController {

    private final ILoginService loginServiceImpl;

    public LoginController(ILoginService loginServiceImpl) {
        this.loginServiceImpl = loginServiceImpl;
    }

    @PostMapping("/login")
    public AjaxResult<TokenVO> login(@RequestBody LoginVO loginVO) {
        String token = loginServiceImpl.login(loginVO.getAccountName(), loginVO.getPassword());
        if (token != null) {
            TokenVO tokenVO = new TokenVO();
            tokenVO.setToken(token);
            return AjaxResult.success(tokenVO);
        }
        return AjaxResult.failed();
    }

}

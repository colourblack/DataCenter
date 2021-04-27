package com.yingf.controller;

import com.yingf.domain.AjaxResult;
import com.yingf.domain.vo.LoginVO;
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

    private static final String USERNAME = "ADMIN";
    private static final String PASSWORD = "ADMIN";
    private static final Integer USER_ID = 1;

    @PostMapping("/login")
    public AjaxResult<String> login(@RequestBody LoginVO loginVO) {
        if (loginVO.getUserId().equals(USER_ID) && loginVO.getUsername().equals(USERNAME)
                && loginVO.getPassword().equals(PASSWORD)) {
            return AjaxResult.success();
        }
        return AjaxResult.failed();
    }

}

package com.yingf.service.impl;

import com.yingf.domain.LoginUserDetails;
import com.yingf.domain.entity.SysAccountInfo;
import com.yingf.mapper.SysAccountInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 8:57 AM
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final static Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final SysAccountInfoMapper sysAccountInfoMapper;

    public UserDetailsServiceImpl(SysAccountInfoMapper sysAccountInfoMapper) {
        this.sysAccountInfoMapper = sysAccountInfoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        SysAccountInfo sysAccountInfo = sysAccountInfoMapper.selectAccount(accountName);
        if (sysAccountInfo == null) {
            log.info("登录用户：{} 不存在.", accountName);
            throw new UsernameNotFoundException("登录用户：" + accountName + " 不存在");
        } else if (sysAccountInfo.getIsDeleted() == 1) {
            log.info("登录用户：{} 已被停用.", accountName);
            throw new UsernameNotFoundException("对不起，您的账号：" + accountName + " 已停用");
        }
        log.debug("用户登陆信息: {}", sysAccountInfo.toString());
        return createUserDetails(sysAccountInfo);
    }

    private UserDetails createUserDetails(SysAccountInfo sysAccountInfo) {
        LoginUserDetails userDetails = new LoginUserDetails();
        userDetails.setSysAccountInfo(sysAccountInfo);
        return userDetails;
    }
}

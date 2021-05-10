package com.yingf.domain;

import com.yingf.domain.entity.SysAccountInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 9:44 AM
 */
public class LoginUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登陆时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 用户信息
     */
    private SysAccountInfo sysAccountInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public SysAccountInfo getSysAccountInfo() {
        return sysAccountInfo;
    }

    public void setSysAccountInfo(SysAccountInfo sysAccountInfo) {
        this.sysAccountInfo = sysAccountInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 获取数据库中保存的后的加密密码
     * 必须重写该方法, 确保UsernamePasswordAuthenticationFilter的attemptAuthentication()能够进行校验
     * @return 数据库中的密码
     */
    @Override
    public String getPassword() {
        return sysAccountInfo.getPassword();
    }

    /**
     * 获取数据库中的用户名
     * @return 数据库中的用户名
     */
    @Override
    public String getUsername() {
        return sysAccountInfo.getAccountName();
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     */
    @Override
    public boolean isEnabled() {
        return this.getSysAccountInfo().getIsDeleted() != 1;
    }
}

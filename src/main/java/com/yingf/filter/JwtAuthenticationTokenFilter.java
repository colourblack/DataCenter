package com.yingf.filter;

import com.yingf.domain.LoginUserDetails;
import com.yingf.service.ITokenService;
import com.yingf.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 9:51 AM
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final ITokenService tokenServiceImpl;

    @Autowired
    public JwtAuthenticationTokenFilter(ITokenService tokenServiceImpl) {
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LoginUserDetails loginUser = tokenServiceImpl.getLoginUser(request);
        if (loginUser != null && SecurityUtil.getAuthentication() != null ) {
            tokenServiceImpl.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

}

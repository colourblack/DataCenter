package com.yingf.service.impl;

import com.yingf.constant.CommonConstant;
import com.yingf.domain.LoginUserDetails;
import com.yingf.service.ITokenService;
import com.yingf.util.DataModelRedisUtil;
import com.yingf.util.SnowflakeIdWorker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 9:54 AM
 */
@Service
public class TokenServiceImpl implements ITokenService {

    /** 令牌自定义标识 */
    @Value("${token.header}")
    String header;

    /** 令牌秘钥 */
    @Value("${token.secret}")
    String secret;

    /** 令牌有效期 默认30分钟 */
    @Value("${token.expireTime}")
    int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 90 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 5 * 60 * 1000L;

    private final DataModelRedisUtil redisUtil;

    private final SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    public TokenServiceImpl(DataModelRedisUtil redisUtil, SnowflakeIdWorker snowflakeIdWorker) {
        this.redisUtil = redisUtil;
        this.snowflakeIdWorker = snowflakeIdWorker;
    }


    @Override
    public LoginUserDetails getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (!StringUtils.isEmpty(token)) {
            Claims claims = parseToken(token);
            // 解析对应的权限以及用户信息
            String uuid = (String) claims.get(CommonConstant.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            return (LoginUserDetails) redisUtil.getValue(userKey);
        }
        return null;
    }

    @Override
    public void setLoginUser(LoginUserDetails loginUser) {
        if (loginUser != null && !StringUtils.isEmpty(loginUser.getToken())) {
            String userKey = getTokenKey(loginUser.getToken());
            redisUtil.setValue(userKey, loginUser);
        }
    }

    @Override
    public void delLoginUser(String token) {
        if (!StringUtils.isEmpty(token)) {
            String userKey = getTokenKey(token);
            redisUtil.delKey(userKey);
        }
    }

    @Override
    public String createToken(LoginUserDetails loginUser) {
        String token = snowflakeIdWorker.nextId() + "";
        loginUser.setToken(token);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CommonConstant.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    @Override
    public void verifyToken(LoginUserDetails loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            String token = loginUser.getToken();
            loginUser.setToken(token);
            refreshToken(loginUser);
        }
    }

    @Override
    public void refreshToken(LoginUserDetails loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisUtil.setValueTimeout(userKey, loginUser, expireTime);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求token
     *
     * @param request http request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (!StringUtils.isEmpty(token) && token.startsWith(CommonConstant.TOKEN_PREFIX)) {
            token = token.replace(CommonConstant.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CommonConstant.LOGIN_TOKEN_KEY + uuid;
    }
}

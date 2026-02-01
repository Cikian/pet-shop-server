package cn.cikian.system.core.security;

import cn.cikian.system.core.utils.RedisCache;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.utils.JwtUtil;
import cn.cikian.system.sys.utils.UserRoleApi;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:07
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserRoleApi userRoleApi;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = getJwtFromRequest(request);
        if (token == null || token.isEmpty()) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 解析token
        String userId;
        LoginUser loginTokenUser = null;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            loginTokenUser = JwtUtil.getSubObject(token);
            userId = loginTokenUser.getUser().getId();
        } catch (Exception e) {
            // token非法，直接放行，让后续的授权规则处理
            filterChain.doFilter(request, response);
            return;
        }
        // 从redis中获取用户信息
        String redisKey = "login:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey, LoginUser.class);
        if (Objects.isNull(loginUser)) {
            // 用户未登录，直接放行，让后续的授权规则处理
            filterChain.doFilter(request, response);
            return;
        }
        // 存入SecurityContextHolder
        // TODO 获取权限信息封装到Authentication中
        Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities().isEmpty() ? userRoleApi.getSecurityAuthorities(userId) : loginUser.getAuthorities();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }


    /**
     * 从请求中提取 JWT
     * 支持从以下位置获取：
     * 1. Authorization header
     * 2. Query parameter
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        // 输出到控制台
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header Name: {}", headerName);
            log.info("Header Value: {}", request.getHeader(headerName));
        }

        // 1. 从 Authorization header 获取
        String token = request.getHeader("authorization");
        if (token != null && !StrUtil.isBlank(token)) {
            return token;
        }

        // 2. 从 query parameter 获取
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }
}

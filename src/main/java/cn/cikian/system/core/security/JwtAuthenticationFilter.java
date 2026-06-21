package cn.cikian.system.core.security;

import cn.cikian.crydis.service.Crydis;
import cn.cikian.system.core.enums.RedisConst;
import cn.cikian.system.core.utils.JwtUtil;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.entity.enmu.BizCode;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Cikian
 * @version 1.4
 * @implNote 彻底移除了 userRoleApi 二次查表逻辑，全权信任 Redis 缓存的自闭环权限链路，大幅降低高并发下的响应耗时
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. 提取请求头中的 token
        String token = getJwtFromRequest(request);

        if (StrUtil.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 解析 JWT 载荷
        String userId;
        try {
            LoginUser login = JwtUtil.getSubObject(token);
            userId = login.getUser().getId();
        } catch (NullPointerException | ExpiredJwtException e) {
            request.setAttribute("FILTER_ERROR_CODE", BizCode.NEED_LOGIN);
            filterChain.doFilter(request, response);
            return;
        } catch (JwtException e) {
            request.setAttribute("FILTER_ERROR_CODE", BizCode.UNAUTHORIZED);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 核心改良：直接从 Redis 中获取携带完整角色及权限列表的实体
        String redisKey = RedisConst.USER_LOGIN_PREFIX + userId;
        LoginUser loginUser = Crydis.getObject(redisKey, LoginUser.class);

        if (loginUser == null) {
            request.setAttribute("FILTER_ERROR_CODE", BizCode.NEED_LOGIN);
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 直接获取内部包装后的 Authorities (内部已通过 permissions 纯字符串列表自动转换生成)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 5. 放行
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("authorization");
        }
        if (token != null && (token.trim().equalsIgnoreCase("undefined") || token.trim().equalsIgnoreCase("null") || token.trim().isEmpty())) {
            token = null;
        }
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam) && !tokenParam.equalsIgnoreCase("undefined") && !tokenParam.equalsIgnoreCase("null")) {
            return tokenParam;
        }
        return null;
    }
}
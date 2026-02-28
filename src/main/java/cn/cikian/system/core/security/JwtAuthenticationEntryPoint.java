package cn.cikian.system.core.security;

import cn.cikian.system.sys.entity.enmu.SysStatus;
import cn.cikian.system.sys.entity.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:09
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("访问受限: [{}] {}", request.getMethod(), request.getRequestURI(), authException);

        Result<?> res = null;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 根据异常类型返回具体的错误消息
        if (authException instanceof org.springframework.security.core.userdetails.UsernameNotFoundException) {
            res = Result.error(SysStatus.NO_USER.code(), SysStatus.NO_USER.message());
            // 使用UserDetailsServiceImpl中定义的具体消息
        } else if (authException instanceof InsufficientAuthenticationException) {
            res = Result.error(SysStatus.UNAUTHORIZED.code(), SysStatus.UNAUTHORIZED.message());
        }
        else if (authException instanceof org.springframework.security.authentication.BadCredentialsException) {
            // 密码错误
            res = Result.error(SysStatus.BAD_PWD.code(), SysStatus.BAD_PWD.message());
        }

        // 如果是凭证过期，可以返回特定消息
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token != null && !token.isEmpty()) {
                // 可以在这里检查token是否过期，并返回更具体的消息
                res = Result.error(SysStatus.NEED_LOGIN.code(), SysStatus.NEED_LOGIN.message());
            }
        }

        objectMapper.writeValue(response.getOutputStream(), res);
    }
}

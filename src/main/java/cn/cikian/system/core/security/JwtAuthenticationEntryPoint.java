package cn.cikian.system.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        log.error("未授权访问: {} {}", request.getMethod(), request.getRequestURI(), authException);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", "认证失败，请登录后访问");
        body.put("data", null);
        body.put("path", request.getServletPath());
        body.put("timestamp", System.currentTimeMillis());

        // 根据异常类型返回具体的错误消息
        if (authException instanceof org.springframework.security.core.userdetails.UsernameNotFoundException) {
            // 使用UserDetailsServiceImpl中定义的具体消息
            body.put("message", authException.getMessage());
        } else if (authException instanceof org.springframework.security.authentication.BadCredentialsException) {
            // 密码错误
            body.put("message", "密码错误");
        }

        // 如果是凭证过期，可以返回特定消息
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token != null && !token.isEmpty()) {
                // 可以在这里检查token是否过期，并返回更具体的消息
                body.put("message", "登录已过期，请重新登录");
            }
        }

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}

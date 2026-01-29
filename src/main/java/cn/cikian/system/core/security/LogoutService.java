package cn.cikian.system.core.security;

import cn.cikian.system.sys.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:12
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final JwtUtil jwtUtil;
    // 如果实现了token黑名单，可以在这里注入

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        // 从请求中获取token
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);

        // 可以将token添加到黑名单
        if (StringUtils.hasText(jwt)) {
            // String username = jwtUtil.getUsernameFromToken(jwt);
            // log.info("用户登出: {}", username);

            // TODO: 实现token黑名单机制
            // tokenBlacklistService.blacklistToken(jwt, username);
        }

        // 清除安全上下文
        SecurityContextHolder.clearContext();
    }
}

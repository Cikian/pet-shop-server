package cn.cikian.system.core.security;

import cn.cikian.system.sys.entity.enmu.BizCode;
import cn.cikian.system.sys.entity.vo.Result;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Cikian
 * @version 1.1
 * @implNote 完美打通并优先消费 JwtAuthenticationFilter 传递的特定 BizCode，避免硬编码类名盲猜
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

        log.error("安全入口拦截到访问受限: [{}] {}, 核心原因: {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 统一保持前后端分离的标准 HTTP 401 未认证状态
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<?> res;

        // 优先读取 JwtAuthenticationFilter 细化捕获并塞入的精准业务状态码
        BizCode filterBizCode = (BizCode) request.getAttribute("FILTER_ERROR_CODE");

        if (filterBizCode != null) {
            // 如果拦截器已经明确给出了因为 Token 损坏/过期的业务结果，直接回显该结果
            res = Result.error(filterBizCode.getCode(), filterBizCode.getDescription());
        } else {
            // 如果拦截器里没有错误码，说明用户根本没有传 Token 头部（纯匿名用户尝试访问受保护资源）
            // 此时再根据传统的 Security 认证异常来进行具体的业务提示兜底
            if (authException instanceof UsernameNotFoundException) {
                res = Result.error(BizCode.NO_USER.getCode(), BizCode.NO_USER.getDescription());
            } else if (authException instanceof BadCredentialsException) {
                // 密码错误
                res = Result.error(BizCode.BAD_PWD.getCode(), BizCode.BAD_PWD.getDescription());
            } else if (authException instanceof InsufficientAuthenticationException) {
                // 凭证缺失
                res = Result.error(BizCode.UNAUTHORIZED.getCode(), "请携带有效的登录凭证访问该资源");
            } else {
                // 其它未被拦截器包裹的系统认证异常
                res = Result.error(BizCode.UNAUTHORIZED.getCode(), authException.getMessage());
            }
        }

        String jsonResult = JSON.toJSONString(res);
        response.getWriter().write(jsonResult);
    }
}
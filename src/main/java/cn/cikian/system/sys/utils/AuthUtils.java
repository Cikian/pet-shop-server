package cn.cikian.system.sys.utils;


import cn.cikian.system.sys.entity.dto.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-03 16:26
 */
public class AuthUtils {

    /**
     * 获取当前登录用户信息
     * @return LoginUser (自定义的用户详情类)
     */
    public static LoginUser getLoginUser() {
        // 1. 从当前线程的上下文中获取认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 判空及权限校验（匿名用户通常是字符串 "anonymousUser"）
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }

        // 3. 如果未登录或类型不匹配，返回 null 或抛出自定义异常
        return null;
    }
}

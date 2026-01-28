package cn.cikian.shop.sys.service;

import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.entity.dto.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SysUserService {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUser getByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void updateLastLogin(Long userId, String ip);

    SysUser register(RegisterRequest registerRequest);
}

package cn.cikian.shop.service;

import cn.cikian.shop.sys.entity.SysRole;
import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.entity.SysUserRole;
import cn.cikian.shop.sys.entity.dto.LoginUser;
import cn.cikian.shop.sys.mapper.SysRoleMapper;
import cn.cikian.shop.sys.mapper.SysUserMapper;
import cn.cikian.shop.sys.mapper.SysUserRoleMapper;
import cn.cikian.shop.sys.utils.UserRoleApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:13
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    UserRoleApi userRoleApi;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户: {}", username);

        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(lqw);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new UsernameNotFoundException("用户已被禁用");
        }

        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new UsernameNotFoundException("用户已被锁定");
        }


        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> userPromission = userRoleApi.getUserPromission(user.getId());
        for (String permission : userPromission) {
            if (permission != null) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        List<String> userRoles = userRoleApi.getUserRoles(user.getId());
        for (String role : userRoles) {
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        return new LoginUser(user, authorities);
    }

    /**
     * 获取用户权限
     * 这里可以根据你的业务逻辑获取用户的角色和权限
     */
    private Collection<? extends GrantedAuthority> getAuthorities(SysUser user) {
        Set<String> authorities = new HashSet<>();
        String userRole = "ADMIN";
        // 添加角色
        authorities.add("ROLE_" + userRole.toUpperCase());

        // 如果是管理员，添加额外权限
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            authorities.addAll(getAdminAuthorities());
        } else if ("USER".equalsIgnoreCase(userRole)) {
            authorities.addAll(getUserAuthorities());
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * 管理员权限
     */
    private Set<String> getAdminAuthorities() {
        return Set.of(
                "USER_READ", "USER_WRITE", "USER_DELETE",
                "PRODUCT_READ", "PRODUCT_WRITE", "PRODUCT_DELETE",
                "ORDER_READ", "ORDER_WRITE", "ORDER_DELETE",
                "CATEGORY_READ", "CATEGORY_WRITE", "CATEGORY_DELETE",
                "SYSTEM_CONFIG_READ", "SYSTEM_CONFIG_WRITE"
        );
    }

    /**
     * 普通用户权限
     */
    private Set<String> getUserAuthorities() {
        return Set.of(
                "PRODUCT_READ",
                "CATEGORY_READ",
                "ORDER_READ", "ORDER_WRITE",
                "CART_READ", "CART_WRITE", "CART_DELETE",
                "PROFILE_READ", "PROFILE_WRITE"
        );
    }
}

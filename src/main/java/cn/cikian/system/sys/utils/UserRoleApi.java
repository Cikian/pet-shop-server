package cn.cikian.system.sys.utils;


import cn.cikian.system.sys.entity.SysRolePermission;
import cn.cikian.system.sys.entity.SysUserRole;
import cn.cikian.system.sys.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-29 11:26
 */

@Component
public class UserRoleApi {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysPermissionMapper permissionMapper;
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    /**
     * 获取用户角色列表
     * @param userId
     * @return
     */
    public List<String> getUserRoles(String userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream().map(SysUserRole::getRoleKey).distinct().toList();
    }

    /**
     * 获取用户权限列表
     * @param userId
     * @return
     */
    public List<String> getUserPromission(String userId) {
        List<String> userRoles = getUserRoles(userId);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<SysRolePermission> lqw = new LambdaQueryWrapper<>();
        lqw.in(SysRolePermission::getRoleKey, userRoles);
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(lqw);
        return rolePermissions.stream().map(SysRolePermission::getPermissionCode).distinct().toList();
    }

    /**
     * 获取用户的Security权限列表
     * @param userId
     * @return
     */
    public Collection<? extends GrantedAuthority> getSecurityAuthorities(String userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> userRoles = this.getUserRoles(userId);
        List<String> userPermissions = this.getUserPromission(userId);
        if (userRoles != null && !userRoles.isEmpty()) {
            for (String role : userRoles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        if (userPermissions != null && !userPermissions.isEmpty()) {
            for (String permission : userPermissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return authorities;
    }

}

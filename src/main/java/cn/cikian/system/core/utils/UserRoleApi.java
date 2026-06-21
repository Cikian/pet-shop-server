package cn.cikian.system.core.utils;

import cn.cikian.system.sys.entity.SysRolePermission;
import cn.cikian.system.sys.entity.SysUserRole;
import cn.cikian.system.sys.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.1
 * @implNote 优化为扁平化单表高性能批量查询，增加可用状态(status/del_flag)的安全判定
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 */
@Component
public class UserRoleApi {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    /**
     * 根据用户ID获取其拥有的合法角色编码列表 (如: ADMIN, GOODS_ADMIN)
     *
     * @param userId 用户ID
     * @return 角色编码列表 (去重且过滤无效状态)
     */
    public List<String> getUserRoles(String userId) {
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysUserRole> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getStatus, 1); // 仅查询正常状态的关联

        List<SysUserRole> userRoles = userRoleMapper.selectList(lqw);

        return userRoles.stream()
                .map(SysUserRole::getRoleKey)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据用户拥有的角色编码列表，批量获取其对应的操作权限编码列表 (如: goods:add, PRODUCT_READ)
     *
     * @param roleKeys 角色编码列表
     * @return 权限编码列表 (去重且过滤无效状态)
     */
    public List<String> getUserPermissionsByRoles(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysRolePermission> lqw = new LambdaQueryWrapper<>();
        lqw.in(SysRolePermission::getRoleKey, roleKeys)
                .eq(SysRolePermission::getStatus, 1)    // 1-启用
                .eq(SysRolePermission::getDelFlag, 0);  // 0-未删除

        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(lqw);

        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }
}
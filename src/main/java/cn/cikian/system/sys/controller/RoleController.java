package cn.cikian.system.sys.controller;


import cn.cikian.system.sys.entity.*;
import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.service.SysPermissionService;
import cn.cikian.system.sys.service.SysRolePermissionService;
import cn.cikian.system.sys.service.SysRoleService;
import cn.cikian.system.sys.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-06 16:39
 */

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRolePermissionService rolePermissionService;

    @GetMapping("/list")
    private Result<Page<SysRole>> queryUserByPage(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<SysRole> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysRole> lqw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(SysRole::getRoleKey, keyword).or().like(SysRole::getRoleName, keyword);
        }

        Page<SysRole> rolePage = roleService.page(page, lqw);
        return Result.ok(rolePage);
    }

    @GetMapping("/permissions")
    private Result<Page<SysPermission>> queryPermissionsByPage(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysPermission> lqw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(SysPermission::getPermissionCode, keyword).or().like(SysPermission::getPermissionName, keyword);
        }

        Page<SysPermission> permissionPage = permissionService.page(page, lqw);
        return Result.ok(permissionPage);
    }

    @GetMapping("/permissionsByRole")
    private Result<Page<SysPermission>> queryPermissionsByRole(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "roleId", defaultValue = "") String roleId) {

        LambdaQueryWrapper<SysRolePermission> lqwRp = new LambdaQueryWrapper<>();
        lqwRp.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rpList = rolePermissionService.list(lqwRp);
        if (rpList.isEmpty()) {
            return Result.ok(new Page<>());
        }
        List<String> permissionIds = rpList.stream().map(SysRolePermission::getPermissionId).toList();

        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysPermission> lqw = new LambdaQueryWrapper<>();
        lqw.in(SysPermission::getId, permissionIds);
        if (keyword != null && !keyword.isEmpty()) {
            lqw.and(wrapper -> wrapper.like(SysPermission::getPermissionName, keyword)
                    .or().like(SysPermission::getPermissionCode, keyword));
        }
        Page<SysPermission> permissionPage = permissionService.page(page, lqw);
        return Result.ok(permissionPage);
    }

    @GetMapping("/permissionsWithoutRole")
    private Result<Page<SysPermission>> queryPermissionsWithoutRole(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "roleId", defaultValue = "") String roleId) {

        LambdaQueryWrapper<SysRolePermission> lqwRp = new LambdaQueryWrapper<>();
        lqwRp.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rpList = rolePermissionService.list(lqwRp);
        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysPermission> lqw = new LambdaQueryWrapper<>();
        if (!rpList.isEmpty()) {
            List<String> permissionIds = rpList.stream().map(SysRolePermission::getPermissionId).toList();
            lqw.notIn(SysPermission::getId, permissionIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            lqw.and(wrapper -> wrapper.like(SysPermission::getPermissionName, keyword)
                    .or().like(SysPermission::getPermissionCode, keyword));
        }
        Page<SysPermission> permissionPage = permissionService.page(page, lqw);
        return Result.ok(permissionPage);
    }

    @PostMapping("/addRole")
    public Result<String> addRole(@RequestBody SysRole sysRole) {
        roleService.save(sysRole);
        return Result.ok("添加成功");
    }

    @PutMapping("/editRole")
    public Result<String> editRole(@RequestBody SysRole sysRole) {
        roleService.updateById(sysRole);
        return Result.ok("编辑成功");
    }

    @DeleteMapping("/deleteRole")
    public Result<String> deleteRole(@RequestParam(name = "id") String id) {
        roleService.removeById(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/addPermission")
    public Result<String> addPermission(@RequestBody SysPermission permission) {
        permissionService.save(permission);
        return Result.ok("添加成功");
    }

    @PutMapping("/editPermission")
    public Result<String> editPermission(@RequestBody SysPermission permission) {
        permissionService.updateById(permission);
        return Result.ok("编辑成功");
    }

    @DeleteMapping("/deletePermission")
    public Result<String> deletePermission(@RequestParam(name = "id") String id) {
        rolePermissionService.removeById(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/addRolePermission")
    public Result<String> addRolePermission(@RequestParam String roleId, @RequestParam String permissionId) {
        SysRole role = roleService.getById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        SysPermission permission = permissionService.getById(permissionId);
        if (permission == null) {
            return Result.error("权限不存在");
        }

        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        rolePermission.setRoleKey(role.getRoleKey());
        rolePermission.setPermissionCode(permission.getPermissionCode());
        rolePermissionService.save(rolePermission);
        return Result.ok("添加成功");
    }

    @DeleteMapping("/deleteRolePermission")
    public Result<String> deleteRolePermission(@RequestParam(name = "roleId") String roleId, @RequestParam(name = "permissionId") String permissionId) {
        LambdaQueryWrapper<SysRolePermission> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysRolePermission::getPermissionId, permissionId);
        lqw.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionService.remove(lqw);
        return Result.ok("删除成功");
    }

    @PostMapping("/addUserRole")
    public Result<String> addUserRole(@RequestParam String userId, @RequestParam String roleId) {
        if (userId == null || userId.isEmpty() || roleId == null || roleId.isEmpty()) {
            return Result.error("参数错误");
        }
        SysRole role = roleService.getById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setRoleKey(role.getRoleKey());
        userRoleService.save(userRole);
        return Result.ok("添加成功");
    }

    @DeleteMapping("/deleteUserRole")
    public Result<String> deleteUserRole(@RequestParam(name = "userId") String userId, @RequestParam(name = "roleId") String roleId) {
        LambdaQueryWrapper<SysUserRole> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUserRole::getUserId, userId);
        lqw.eq(SysUserRole::getRoleId, roleId);
        userRoleService.remove(lqw);
        return Result.ok("删除成功");
    }
}

package cn.cikian.system.sys.controller;


import cn.cikian.system.sys.entity.SysRole;
import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.SysUserRole;
import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.entity.vo.UserVO;
import cn.cikian.system.sys.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-06 14:37
 */

@RestController
@RequestMapping("/api/user")
public class UserInfoController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysUserRoleService userRoleService;


    @GetMapping("/list")
    private Result<Page<UserVO>> queryUserByPage(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<SysUser> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getEmail, keyword)
                    .or().like(SysUser::getSex, keyword)
                    .or().like(SysUser::getUserSource, keyword);
        }

        Page<UserVO> userVoPage = userService.pageUserMode(page, lqw);
        return Result.ok(userVoPage);
    }

    @GetMapping("/byRole")
    private Result<Page<UserVO>> getUserInfoByRole(@RequestParam String roleId,
                                                   @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<SysUserRole> lqwSy = new LambdaQueryWrapper<>();
        lqwSy.eq(SysUserRole::getRoleId, roleId);
        List<SysUserRole> userRoles = userRoleService.list(lqwSy);
        if (userRoles.isEmpty()) {
            return Result.ok(new Page<>());
        }
        List<String> userIds = userRoles.stream().map(SysUserRole::getUserId).toList();
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.in(SysUser::getId, userIds);
        if (keyword != null && !keyword.isEmpty()) {
            lqw.and(lqw1 -> lqw1.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getEmail, keyword)
                    .or().like(SysUser::getSex, keyword)
                    .or().like(SysUser::getUserSource, keyword));
        }
        Page<UserVO> userVoPage = userService.pageUserMode(new Page<>(pageNo, pageSize), lqw);
        return Result.ok(userVoPage);
    }

    @GetMapping("/withoutRole")
    private Result<Page<UserVO>> getUserInfoWithoutRole(@RequestParam String roleId,
                                                        @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<SysUserRole> lqwSy = new LambdaQueryWrapper<>();
        lqwSy.eq(SysUserRole::getRoleId, roleId);
        List<SysUserRole> userRoles = userRoleService.list(lqwSy);
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        if (!userRoles.isEmpty()) {
            List<String> userIds = userRoles.stream().map(SysUserRole::getUserId).toList();
            lqw.notIn(SysUser::getId, userIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            lqw.and(lqw1 -> lqw1.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getEmail, keyword)
                    .or().like(SysUser::getSex, keyword)
                    .or().like(SysUser::getUserSource, keyword));
        }
        Page<UserVO> userVoPage = userService.pageUserMode(new Page<>(pageNo, pageSize), lqw);
        return Result.ok(userVoPage);
    }
}

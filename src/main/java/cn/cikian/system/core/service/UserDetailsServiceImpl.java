package cn.cikian.system.core.service;

import cn.cikian.system.core.exception.CikException;
import cn.cikian.system.core.utils.UserRoleApi;
import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.entity.enmu.BizCode;
import cn.cikian.system.sys.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Cikian
 * @version 1.1
 * @implNote 在登录阶段一步到位加载全部角色与具体权限，拒绝后续请求的二次回表
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final UserRoleApi userRoleApi; // 注入重构后的角色权限API

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        if (user.getStatus() != null) {
            switch (user.getStatus()) {
                case 2:
                    throw new CikException(BizCode.USER_FREEZE); // 抛出冻结异常
                case 3:
                    throw new CikException(BizCode.USER_BANNED); // 抛出永久封禁异常
                case 1:
                    break; // 正常，放行
                default:
                    throw new CikException(BizCode.USER_DISABLED); // 其它未知禁用状态
            }
        }

        // 2. 一次性获取用户的所有权限与角色（这里将模拟逻辑改造为真实的合并注入）
        List<String> totalPermissions = StreamAllAuthorities(user);

        // 3. 封装成完整的 LoginUser 返回，后续框架会自动将其存入 Redis 缓存
        return new LoginUser(user, totalPermissions);
    }

    /**
     * 核心完善：合并获取用户的动态角色与操作权限，完全平替原有硬编码方法
     */
    private List<String> StreamAllAuthorities(SysUser user) {
        Set<String> auths = new HashSet<>();
        String userIdStr = String.valueOf(user.getId());

        // 1. 从数据库捞出该用户分配的所有合法 role_key (例如: [ADMIN, GOODS_ADMIN])
        List<String> userRoles = userRoleApi.getUserRoles(userIdStr);

        if (userRoles != null && !userRoles.isEmpty()) {
            for (String roleKey : userRoles) {
                // 统一为 Spring Security 注入带标准前缀的角色识别凭证
                auths.add("ROLE_" + roleKey.toUpperCase().trim());
            }

            // 2. 根据捞出的角色列表，一次性批量捞出对应绑定的所有有效 permission_code
            List<String> dbPermissions = userRoleApi.getUserPermissionsByRoles(userRoles);
            if (dbPermissions != null && !dbPermissions.isEmpty()) {
                auths.addAll(dbPermissions);
            }
        }

        log.debug("用户 [{}] 登录成功，从数据库动态加载的 Authorities 授权总览: {}", user.getUsername(), auths);
        return new ArrayList<>(auths);
    }
}
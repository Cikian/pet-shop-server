package cn.cikian.system.sys.service.impl;


import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.entity.dto.RegisterRequest;
import cn.cikian.system.sys.mapper.SysUserMapper;
import cn.cikian.system.sys.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:34
 */

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername, username);
        return userMapper.selectOne(lqw);
    }

    @Override
    public boolean existsByUsername(String username) {
        SysUser user = getByUsername(username);
        return user != null;
    }

    @Override
    public SysUser getByEmail(String email) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getEmail, email);
        return userMapper.selectOne(lqw);
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getEmail, email);
        return userMapper.selectOne(lqw) != null;
    }

    @Override
    public void updateLastLogin(String userId, String ip) {
        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<SysUser> luw = new LambdaUpdateWrapper<>();
        luw.eq(SysUser::getId, userId).set(SysUser::getLastLoginIp, ip);
        luw.set(SysUser::getLastLoginTime, now);
        luw.set(SysUser::getLastLoginIp, ip);
        userMapper.update(null, luw);

    }

    @Override
    public SysUser register(RegisterRequest registerRequest) {
        boolean b = existsByUsername(registerRequest.getUsername());
        if (b) {
            throw new RuntimeException("用户名已存在");
        }
        boolean b1 = existsByEmail(registerRequest.getEmail());
        if (b1) {
            throw new RuntimeException("邮箱已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setNickname(registerRequest.getNickname());
        user.setAvatar(registerRequest.getAvatar());
        userMapper.insert(user);
        return user;
    }

    @Override
    public SysUser createOrUpdateGoogleUser(String email, String username, String nickname, String avatar) {
        // 先根据邮箱查询用户是否存在
        SysUser user = getByEmail(email);
        if (user != null) {
            // 如果用户存在，更新用户信息
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setLoginSource("google");
            userMapper.updateById(user);
        } else {
            // 如果用户不存在，创建新用户
            user = new SysUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setLoginSource("google");
            user.setPassword(passwordEncoder.encode("google_oauth2_" + System.currentTimeMillis())); // 生成随机密码
            userMapper.insert(user);
        }
        return user;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        // TODO: 从数据库中查询用户的权限
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // 构建LoginUser
        return new LoginUser(user, authorities);
    }
}

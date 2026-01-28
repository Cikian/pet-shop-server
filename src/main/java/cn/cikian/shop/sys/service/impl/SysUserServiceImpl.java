package cn.cikian.shop.sys.service.impl;


import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.entity.dto.RegisterRequest;
import cn.cikian.shop.sys.mapper.SysUserMapper;
import cn.cikian.shop.sys.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername, username);
        return userMapper.selectOne(lqw);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = getByUsername(username);
        if (user != null) {
            return user;
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        SysUser user = getByUsername(username);
        return user != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getEmail, email);
        return userMapper.selectOne(lqw) != null;
    }

    @Override
    public void updateLastLogin(Long userId, String ip) {
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
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setNickname(registerRequest.getNickname());
        user.setAvatar(registerRequest.getAvatar());
        userMapper.insert(user);
        return user;
    }
}

package cn.cikian.system.sys.service;

import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.dto.RegisterRequest;

public interface SysUserService {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUser getByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    SysUser getByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void updateLastLogin(String userId, String ip);

    SysUser register(RegisterRequest registerRequest);

    /**
     * 创建或更新谷歌登录用户
     * @param email 邮箱
     * @param username 用户名
     * @param nickname 昵称
     * @param avatar 头像
     * @return
     */
    SysUser createOrUpdateGoogleUser(String email, String username, String nickname, String avatar);
}

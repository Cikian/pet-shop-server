package cn.cikian.system.sys.entity.dto;

import cn.cikian.system.sys.entity.SysUser;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.1
 * @implNote 统一由 permissions 字符串列表接管角色(ROLE_开头的字符串)与操作权限，实现极致的高性能全缓存架构
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "登录用户视图")
public class LoginUser implements UserDetails {

    @Schema(description = "用户实体")
    private SysUser user;

    @Schema(description = "包含角色(ROLE_开头的字符串)和具体操作权限的统一列表")
    private List<String> permissions = new ArrayList<>();

    // 内部运行时缓存，不参与任何序列化传输
    @JSONField(serialize = false, deserialize = false)
    private transient List<GrantedAuthority> cachedAuthorities;

    public LoginUser(SysUser user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 利用内存缓存，避免单次请求上下文中重复 map 转换造成的微小损耗
        if (cachedAuthorities != null) {
            return cachedAuthorities;
        }

        if (permissions == null || permissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 一次性把所有的 角色字符串(ROLE_ADMIN) 和 权限字符串(USER_READ) 统一转为 Security 专用的 Authority 对象
        this.cachedAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return this.cachedAuthorities;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isAccountNonExpired() {
        // 健壮性保护：若 user 实例尚未初始化完全，默认不放行
        return user != null;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isAccountNonLocked() {
        // 🔒 消除隐患：user 为 null 时直接返回 false（视为锁定），杜绝策略绕过
        if (user == null) {
            return false;
        }
        // 2-冻结：如果状态是 2，说明被锁定了，返回 false 触发 LockedException
        if (user.getStatus() != null && user.getStatus() == 2) {
            return false;
        }
        // 3-永久封禁：同样可以视为账户锁定
        return user.getStatus() != null && user.getStatus() != 3;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isCredentialsNonExpired() {
        return user != null;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isEnabled() {
        if (user == null) {
            return false; // 🔒 默认拒绝
        }
        // 1-正常：只有精确匹配到 1 时，用户才是“已启用”状态
        return user.getStatus() != null && user.getStatus() == 1;
    }

}
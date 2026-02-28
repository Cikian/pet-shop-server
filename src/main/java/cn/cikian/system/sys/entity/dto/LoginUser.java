package cn.cikian.system.sys.entity.dto;

import cn.cikian.system.sys.entity.SysUser;
import com.alibaba.fastjson.annotation.JSONField;
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

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:30
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "登录用户视图")
public class LoginUser implements UserDetails {
    @Schema(description = "用户实体")
    private SysUser user;
    @Schema(description = "权限列表")
    private List<String> permissions;
    @JSONField(serialize = false)
    @Schema(description = "Security权限列表")
    private List<GrantedAuthority> authorities;

    public LoginUser(SysUser user) {
        this.user = user;
    }

    public LoginUser(SysUser user, List<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (authorities != null) {
            authorityList.addAll(authorities);
        }
        if (permissions != null) {
            // 把permissions中字符串类型的权限信息转换成GrantedAuthority对象存入authorities中
            for (String permission : permissions) {
                authorityList.add(new SimpleGrantedAuthority(permission));
            }
        }
        return authorityList;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

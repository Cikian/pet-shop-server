package cn.cikian.system.sys.entity.vo;


import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.dto.LoginUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:33
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "用户信息视图")
public class UserVO {
    @Schema(description = "用户ID")
    private String id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像")
    private String avatar;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "生日")
    private Date birthday;
    @Schema(description = "性别（1：男 2：女）")
    private Integer sex;
    @Schema(description = "电子邮件")
    private String email;
    @Schema(description = "电话号码")
    private String phone;
    @Schema(description = "用户来源")
    private String userSource;
    @Schema(description = "状态（1：正常 0：禁用）")
    private Integer status;
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public UserVO(SysUser user) {
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.nickname = user.getNickname();
//        this.avatar = user.getAvatar();
//        this.birthday = user.getBirthday();
//        this.sex = user.getSex();
//        this.email = user.getEmail();
//        this.phone = user.getPhone();
//        this.status = user.getStatus();
//        this.lastLoginTime = user.getLastLoginTime();
//        this.lastLoginIp = user.getLastLoginIp();

        BeanUtils.copyProperties(user, this);
    }

    public UserVO(LoginUser loginUser) {
        SysUser user = loginUser.getUser();
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.nickname = user.getNickname();
//        this.avatar = user.getAvatar();
//        this.birthday = user.getBirthday();
//        this.sex = user.getSex();
//        this.email = user.getEmail();
//        this.phone = user.getPhone();
//        this.status = user.getStatus();
//        this.lastLoginTime = user.getLastLoginTime();
//        this.lastLoginIp = user.getLastLoginIp();

        BeanUtils.copyProperties(user, this);
    }
}

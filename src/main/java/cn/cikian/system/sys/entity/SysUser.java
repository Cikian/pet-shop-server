package cn.cikian.system.sys.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 15:44
 */

@Data
@TableName("sys_user")
@Schema(name = "用户实体")
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "生日")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @Schema(description = "性别（1：男 2：女）")
    private Integer sex;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "登录来源")
    private String userSource;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "状态（0：正常 1：冻结）")
    private Integer status;

    @Schema(description = "删除标记")
    @TableLogic
    private Boolean delFlag;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "上次登录IP")
    private String lastLoginIp;
    private LocalDateTime createTime;

}

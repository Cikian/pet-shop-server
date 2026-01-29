package cn.cikian.shop.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:18
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     *
     */
    private String roleKey;
    /**
     *
     */
    private Long roleId;
    /**
     *
     */
    private Long permissionId;
    /**
     *
     */
    @Length(max = 256, message = "编码长度不能超过256")
    private String permissionCode;
    /**
     *
     */
    private Integer delFlag;
}

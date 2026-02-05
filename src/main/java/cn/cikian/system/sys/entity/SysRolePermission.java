package cn.cikian.system.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
    private String id;
    /**
     *
     */
    private String roleKey;
    /**
     *
     */
    private String roleId;
    /**
     *
     */
    private String permissionId;
    /**
     *
     */
    private String permissionCode;
    /**
     * 1: 启用
     */
    private Integer status;
    /**
     *
     */
    @TableLogic
    private Integer delFlag;
}

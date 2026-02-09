package cn.cikian.system.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
*
* @TableName sys_user_role
*/

@Data
@TableName("sys_user_role")
@Schema(name = "用户角色关联实体")
public class SysUserRole implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "角色KEY")
    private String roleKey;

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "状态：1-启用")
    private Integer status;


}

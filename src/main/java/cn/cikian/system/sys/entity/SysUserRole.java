package cn.cikian.system.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
*
* @TableName sys_user_role
*/

@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    /**
    *
    */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
    *
    */
    private String userId;
    /**
    *
    */
    private String roleKey;
    /**
    *
    */
    private String roleId;
    /**
    * 1: 启用
    */
    private Integer status;


}

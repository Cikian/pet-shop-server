package cn.cikian.system.sys.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
*
* @TableName sys_role
*/

@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    /**
    *
    */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
    *
    */
    private String roleName;
    /**
    *
    */
    private String roleKey;
    /**
    * 1:正常 2:禁用
    */
    private Integer status;
    /**
    *
    */
    private Integer delFlag;


}

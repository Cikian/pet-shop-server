package cn.cikian.shop.sys.entity;

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
    private Long id;
    /**
    *
    */
    private Long userId;
    /**
    *
    */
    private String roleKey;
    /**
    *
    */
    private Long roleId;
    /**
    * 1: 启用
    */
    private Integer status;


}

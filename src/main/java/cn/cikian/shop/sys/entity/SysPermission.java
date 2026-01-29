package cn.cikian.shop.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:18
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     *
     */
    private String permissionName;
    /**
     *
     */
    private String permissionCode;
    /**
     *
     */
    private Integer status;
    /**
     *
     */
    private Integer delFlag;

}

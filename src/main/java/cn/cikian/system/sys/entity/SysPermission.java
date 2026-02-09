package cn.cikian.system.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "权限实体")
public class SysPermission implements Serializable {

    @Schema(description = "权限ID")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "状态（1：正常 0：禁用）")
    private Integer status;

    @Schema(description = "删除标记")
    @TableLogic
    private Integer delFlag;

}

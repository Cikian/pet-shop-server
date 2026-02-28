package cn.cikian.system.sys.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role")
@Schema(name = "角色实体")
public class SysRole implements Serializable {

    @Schema(description = "角色ID")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色键")
    private String roleKey;

    @Schema(description = "状态（1：正常 0：禁用）")
    private Integer status;

    @Schema(description = "删除标记")
    @TableLogic
    private Integer delFlag;


}

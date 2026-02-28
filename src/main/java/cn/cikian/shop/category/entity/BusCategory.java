package cn.cikian.shop.category.entity;

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
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_category")
@Schema(name = "商品分类实体")
public class BusCategory implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分类 ID")
    private String id;
    /**
     *
     */
    @Schema(description = "分类名称")
    private String name;
    /**
     *
     */
    @Schema(description = "分类编码")
    private String cateCode;
    /**
     * 图片访问地址
     */
    @Schema(description = "图片")
    private String imgUrl;
    /**
     * 1-启用
     */
    @Schema(description = "状态（1-启用 0-禁用）")
    private Integer status;
    /**
     * 是否首页推荐
     */
    @Schema(description = "是否首页推荐")
    private Boolean onHome;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

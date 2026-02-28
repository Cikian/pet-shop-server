package cn.cikian.shop.product.entity;

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
@TableName("bus_product_img")
@Schema(name = "商品附图实体")
public class ProductImg implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;
    /**
     *
     */
    @Schema(description = "商品ID")
    private String productId;
    /**
     * 图片描述
     */
    @Schema(description = "图片描述")
    private String description;
    /**
     * 图片链接
     */
    @Schema(description = "图片链接")
    private String imgUrl;
    /**
     *
     */
    @Schema(description = "排序")
    private Integer sortOrder;
    /**
     * 状态（1-启用 0-禁用）
     */
    @Schema(description = "状态（1-启用 0-禁用）")
    private Boolean status;
    /**
     *
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

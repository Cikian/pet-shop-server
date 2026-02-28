package cn.cikian.shop.sku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_sku")
@Schema(name = "商品SKU实体")
public class BusSku implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "SKU ID")
    private String id;
    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private String productId;
    /**
     * SKU名称
     */
    @Schema(description = "SKU名称")
    private String name;
    /**
     * SKU编码
     */
    @Schema(description = "SKU编码")
    private String skuCode;
    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal price;
    /**
     * 原价
     */
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    /**
     * 库存
     */
    @Schema(description = "库存")
    private Integer stock;
    /**
     * 库存预警值
     */
    @Schema(description = "库存预警值")
    private Integer warningStock;
    /**
     * 是否为默认SKU
     */
    @Schema(description = "是否为默认SKU")
    private Boolean isDefault;
    /**
     * 1-启用 0-禁用
     */
    @Schema(description = "1-启用 0-禁用")
    private Boolean status;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

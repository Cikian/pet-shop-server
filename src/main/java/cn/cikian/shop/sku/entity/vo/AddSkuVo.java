package cn.cikian.shop.sku.entity.vo;


import cn.cikian.shop.sku.entity.SkuSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-02 14:54
 */

@Data
@Schema(name = "添加SKU请求实体")
public class AddSkuVo {
    @Schema(description = "ID")
    private String id;
    // 商品ID
    @Schema(description = "商品ID")
    private String productId;
    // SKU名称
    @Schema(description = "SKU名称")
    private String name;
    // SKU编码
    @Schema(description = "SKU编码")
    private String skuCode;
    // 价格
    @Schema(description = "价格")
    private BigDecimal price;
    // 原价
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    // 库存
    @Schema(description = "库存")
    private Integer stock;
    // 库存预警值
    @Schema(description = "库存预警值")
    private Integer warningStock;
    // 是否为默认SKU
    @Schema(description = "是否为默认SKU")
    private Boolean isDefault;
    // sku与规格关系
    @Schema(description = "sku与规格关系")
    private List<SkuSpec> skuSpecs;
}

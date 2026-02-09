package cn.cikian.shop.product.entity.vo;


import cn.cikian.shop.sku.entity.vo.AddSkuVo;
import cn.cikian.shop.spec.entity.vo.AddSpecVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-01 04:10
 */

@Data
@Schema(name = "新增商品对象")
public class AddProductVo {
    @Schema(description = "商品ID")
    private String id;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品描述")
    private String description;
    @Schema(description = "商品分类ID")
    private String categoryId;
    @Schema(description = "商品状态：1-上架，0-下架")
    private Integer status;
    @Schema(description = "外站链接")
    private String outSiteUrl;
    @Schema(description = "商品来源")
    private String productSource;
    @Schema(description = "商品主图")
    private MultipartFile mainImg;
    @Schema(description = "商品标签，多个标签用逗号分隔")
    private String tags;
    // 商品附图
    @Schema(description = "商品附图")
    private List<ProductImgVo> pictures;
    // 规格列表
    @Schema(description = "规格列表")
    private List<AddSpecVo> specs;
    // SKU列表
    @Schema(description = "SKU列表")
    private List<AddSkuVo> skus;
}

package cn.cikian.shop.product.entity.vo;


import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.entity.SkuSpec;
import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.SpecValues;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-02 10:13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product4Detail {
    private String id;
    private String name;
    private String description;
    private String mainImg;
    private String categoryId;
    private Integer status;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private float score;

    private List<String> tags;
    private List<SpecVo> specs;
    private List<SkuVo> skus;
    private List<String> images;

    public Product4Detail(BusProduct product,
                          List<BusSku> skus,
                          List<SpecKeys> specKeys,
                          List<SpecValues> specValues,
                          List<SkuSpec> skuSpecs,
                          List<ProductTag> tags) {

        BeanUtils.copyProperties(product, this);
        this.tags = tags.stream().map(ProductTag::getTagName).collect(Collectors.toList());

    }
}

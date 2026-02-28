package cn.cikian.shop.product.entity.vo;


import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.sku.entity.BusSku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-28 15:34
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeProductVo {
    private String id;
    private String name;
    private String mainImg;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Float score;
    private List<String> tags;
    private String discount;

    private HomeProductVo(BusProduct product, List<BusSku> skuList, List<ProductTag> tagsByProduct, BusSku minPriceSku) {
        BeanUtils.copyProperties(product, this);
        this.tags = tagsByProduct.stream()
                .map(ProductTag::getTagName)
                .collect(Collectors.toList());

        this.price = minPriceSku.getPrice();
        this.originalPrice = minPriceSku.getOriginalPrice();

        // 计算折扣：降价的百分比
        this.discount = String.format("%.1f", (1 - this.price.divide(this.originalPrice, RoundingMode.CEILING).floatValue()) * 100) + "%";

        // todo: 暂时未引入评分系统，在3.5到5的范围内，随机给出一个数值，可以是整数或1位小数，注意最多只允许1位小数
        int randomInt = ThreadLocalRandom.current().nextInt(35, 51);
        this.score = randomInt / 10.0f;
    }

    public static HomeProductVo create(BusProduct product, List<BusSku> skuList, List<ProductTag> tagsByProduct) {
        return skuList.stream()
                .min(Comparator.comparing(BusSku::getPrice))
                .map(minSku -> new HomeProductVo(product, skuList, tagsByProduct, minSku))
                .orElse(null);
    }
}

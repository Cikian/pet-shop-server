package cn.cikian.shop.product.entity.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-02 10:50
 */

@Data
public class SkuVo {
    private String id;
    private String name;
    private String skuCode;
    private List<Map<String, String>> specs;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Boolean isDefault;
}

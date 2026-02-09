package cn.cikian.shop.product.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-09 15:06
 */

@Data
public class Slideshow implements Serializable {
    private Long id;
    @ApiModelProperty("商品id")
    private Long productId;
    @ApiModelProperty("1-启用 0-禁用")
    private Integer status;
    @ApiModelProperty("排序")
    private Integer sortOrder;
    private Integer delFlag;
}

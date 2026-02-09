package cn.cikian.shop.product.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
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
public class HomeSlideshow implements Serializable {
    private Long id;
    @ApiModelProperty("商品ID")
    private Long productId;
    @ApiModelProperty("排序")
    private Integer sortOrder;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品描述")
    private String description;
    @ApiModelProperty("主图")
    private String mainImg;
    @ApiModelProperty("商品分类")
    private Long categoryId;
    @ApiModelProperty("状态：1-启用")
    private Integer status;
    @TableLogic
    private Integer delFlag;
}

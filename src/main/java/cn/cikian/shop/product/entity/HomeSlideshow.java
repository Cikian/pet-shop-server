package cn.cikian.shop.product.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("vo_home_slideshow")
public class HomeSlideshow implements Serializable {
    private String id;
    @ApiModelProperty("商品ID")
    private String productId;
    @ApiModelProperty("排序")
    private Integer sortOrder;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品描述")
    private String description;
    @ApiModelProperty("展示图")
    private String displayImg;
    @ApiModelProperty("主图")
    private String mainImg;
    @ApiModelProperty("商品分类")
    private String categoryId;
    @ApiModelProperty("状态：1-启用")
    private Boolean status;
    @TableLogic
    private Boolean delFlag;
}

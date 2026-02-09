package cn.cikian.shop.product.entity;

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
 * @since 2026-02-09 15:06
 */

@Data
@TableName("vo_home_slideshow")
@Schema(name = "首页轮播图视图实体")
public class HomeSlideshow implements Serializable {
    private String id;
    @Schema(description = "商品ID")
    private String productId;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品描述")
    private String description;
    @Schema(description = "展示图")
    private String displayImg;
    @Schema(description = "主图")
    private String mainImg;
    @Schema(description = "商品分类")
    private String categoryId;
    @Schema(description = "状态：1-启用")
    private Boolean status;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

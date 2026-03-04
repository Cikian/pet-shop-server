package cn.cikian.shop.cart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-04 16:55
 */

@Data
@TableName("vo_cart_product")
@Schema(name = "购物车视图")
public class CartVo implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;
    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String productName;
    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private String productId;
    /**
     * 主图
     */
    @Schema(description = "主图")
    private String mainImg;
    /**
     * SKU名称
     */
    @Schema(description = "SKU名称")
    private String skuName;
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
     * 购买数量
     */
    @Schema(description = "购买数量")
    private Integer quantity;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;
    /**
     *
     */
    @Schema(description = "创建时间")
    private Date createTime;
    @TableLogic
    private Boolean delFlag;
}

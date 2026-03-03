package cn.cikian.shop.cart.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-03 15:53
 */

@Data
@TableName("bus_cart")
@Schema(name = "购物车实体")
public class BusCart implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分类 ID")
    private String id;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;
    /**
     * 商品SKU-ID
     */
    @Schema(description = "商品SKU-ID")
    private String skuId;
    /**
     * 购买数量
     */
    @Schema(description = "购买数量")
    private Integer quantity;
    /**
     * 是否选中（用于结算页）
     */
    @Schema(description = "是否选中（用于结算页）")
    private Boolean isChecked;
    /**
     *
     */
    @Schema(description = "创建时间")
    private Date createTime;
    /**
     *
     */
    @Schema(description = "更新时间")
    private Date updateTime;
    /**
     *
     */
    @TableLogic
    private Boolean delFlag;
}

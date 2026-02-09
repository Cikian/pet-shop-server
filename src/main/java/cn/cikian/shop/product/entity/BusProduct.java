package cn.cikian.shop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_product")
@Schema(name = "商品实体")
public class BusProduct implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "商品ID")
    private String id;
    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String name;
    /**
     * 商品描述
     */
    @Schema(description = "商品描述")
    private String description;
    /**
     * 主图
     */
    @Schema(description = "主图")
    private String mainImg;
    /**
     * 商品分类
     */
    @Schema(description = "商品分类")
    private String categoryId;
    /**
     * 外站链接
     */
    @Schema(description = "外站链接")
    private String outSiteUrl;
    /**
     * 商品来源
     */
    @Schema(description = "商品来源")
    private String productSource;
    /**
     * 状态：1-上架，0-下架
     */
    @Schema(description = "状态：1-上架，0-下架")
    private Integer status;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;

}

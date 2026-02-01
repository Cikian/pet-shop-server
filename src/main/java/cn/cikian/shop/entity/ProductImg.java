package cn.cikian.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_product_img")
public class ProductImg implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     *
     */
    private String productId;
    /**
     * 图片描述
     */
    private String description;
    /**
     * 图片链接
     */
    private String imgUrl;
    /**
     *
     */
    private Integer sortOrder;
    /**
     * 状态（1-启用 0-禁用）
     */
    private Boolean status;
    /**
     *
     */
    private Boolean delFlag;
}

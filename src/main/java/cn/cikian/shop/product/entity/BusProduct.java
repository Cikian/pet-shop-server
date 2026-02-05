package cn.cikian.shop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class BusProduct implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 主图
     */
    private String mainImg;
    /**
     * 商品分类
     */
    private String categoryId;
    /**
     * 状态：1-上架，0-下架
     */
    private Integer status;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Boolean delFlag;

}

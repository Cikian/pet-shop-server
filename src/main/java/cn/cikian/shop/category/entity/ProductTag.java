package cn.cikian.shop.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-03 17:33
 */

@Data
@TableName("bus_tags_product")
public class ProductTag implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 商品ID
     */
    private String productId;
    /**
     * tagId
     */
    private String tagId;
    /**
     * 分类
     */
    private String category;
}

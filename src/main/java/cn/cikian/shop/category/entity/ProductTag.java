package cn.cikian.shop.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2026-02-03 17:33
 */

@Data
@TableName("bus_tags_product")
@Schema(name = "商品标签关联实体")
public class ProductTag implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private String productId;
    /**
     * tagId
     */
    @Schema(description = "标签ID")
    private String tagId;
    /**
     * 分类
     */
    @Schema(description = "分类")
    private String category;
}

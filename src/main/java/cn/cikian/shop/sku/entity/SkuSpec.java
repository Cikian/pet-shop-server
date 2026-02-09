package cn.cikian.shop.sku.entity;

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
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_sku_spec")
@Schema(name = "SKU规格关联实体")
public class SkuSpec implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "SKU规格关联ID")
    private String id;
    /**
     * SKU ID
     */
    @Schema(description = "SKU ID")
    private String skuId;
    /**
     * 规格键ID
     */
    @Schema(description = "规格键ID")
    private String specKeyId;
    /**
     * 规格值ID
     */
    @Schema(description = "规格值ID")
    private String specValueId;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

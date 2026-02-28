package cn.cikian.shop.spec.entity;

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
@TableName("bus_spec_values")
@Schema(name = "规格值实体")
public class SpecValues implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "规格值ID")
    private String id;
    /**
     * 规格键ID
     */
    @Schema(description = "规格键ID")
    private String specKeyId;
    /**
     * 规格值，如：红色、16G、500ml
     */
    @Schema(description = "规格值，如：红色、16G、500ml")
    private String value;
    /**
     * 规格图片（如颜色色卡）
     */
    @Schema(description = "规格图片（如颜色色卡）")
    private String image;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

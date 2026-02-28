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
@TableName("bus_spec_keys")
@Schema(name = "商品规格键实体")
public class SpecKeys implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "规格ID")
    private String id;
    /**
     * 规格名称，如：颜色、尺寸、容量
     */
    @Schema(description = "规格名称，如：颜色、尺寸、容量")
    private String name;
    /**
     * 关联的商品ID（可为空，表示通用规格）
     */
    @Schema(description = "关联的商品ID（可为空，表示通用规格）")
    private String productId;
    /**
     * 输入类型
     */
    @Schema(description = "输入类型")
    private String inputType;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

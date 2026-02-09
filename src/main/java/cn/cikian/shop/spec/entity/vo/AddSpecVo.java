package cn.cikian.shop.spec.entity.vo;


import cn.cikian.shop.spec.entity.SpecValues;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-02 15:32
 */

@Data
@Schema(name = "新增规格参数对象")
public class AddSpecVo {
    @Schema(description = "id")
    private String id;
    // 规格名称，如：颜色、尺寸、容量
    @Schema(description = "规格名称，如：颜色、尺寸、容量")
    private String name;
    // 关联的分类ID（可为空，表示通用规格）
    @Schema(description = "关联的分类ID（可为空，表示通用规格）")
    private String productId;
    // 输入类型
    @Schema(description = "输入类型")
    private String inputType;
    // 排序
    @Schema(description = "排序")
    private Integer sortOrder;
    // 规格值列表
    @Schema(description = "规格值列表")
    List<SpecValues> specValueList;
}

package cn.cikian.shop.product.entity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-01 05:15
 */

@Data
@Schema(name = "商品图片VO")
public class ProductImgVo {
    @Schema(description = "图片ID")
    private String id;
    // 图片描述
    @Schema(description = "图片描述")
    private String description;
    // 图片
    @Schema(description = "图片")
    private MultipartFile img;
    // 排序
    @Schema(description = "排序")
    private Integer sortOrder;
}

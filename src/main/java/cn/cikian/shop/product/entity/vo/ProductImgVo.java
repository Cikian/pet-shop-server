package cn.cikian.shop.product.entity.vo;


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
public class ProductImgVo {
    private String id;
    // 图片描述
    private String description;
    // 图片
    private MultipartFile img;
    // 排序
    private Integer sortOrder;
}

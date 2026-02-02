package cn.cikian.shop.entity.vo;


import cn.cikian.shop.entity.ProductImg;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-01 04:10
 */

@Data
public class AddProductVo {
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private Integer status;
    private MultipartFile mainImg;
    // 商品附图
    private List<ProductImgVo> pictures;
    // 规格列表
    private List<AddSpecVo> specs;
}

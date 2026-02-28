package cn.cikian.shop.category.service;

import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-03 17:33
 */

public interface BusTagsService extends IService<BusTags> {
    List<ProductTag> getTagsByProduct(List<String> productIds);
    List<ProductTag> getTagsByProduct(String productId);
}

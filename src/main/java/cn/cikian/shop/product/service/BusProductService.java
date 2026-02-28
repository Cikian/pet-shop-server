package cn.cikian.shop.product.service;

import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.product.entity.vo.HomeProductVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */
public interface BusProductService extends IService<BusProduct> {
    List<HomeProductVo> buildHomeRecommendList(List<BusProduct> products);
}

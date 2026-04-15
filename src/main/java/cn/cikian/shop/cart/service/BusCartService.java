package cn.cikian.shop.cart.service;

import cn.cikian.shop.cart.entity.BusCart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-03 15:53
 */

public interface BusCartService extends IService<BusCart> {
    BusCart getCartByUserAndSku(String userId, String skuId);

    void updateCartById(BusCart cart);
}

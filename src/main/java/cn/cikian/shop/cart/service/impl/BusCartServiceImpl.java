package cn.cikian.shop.cart.service.impl;

import cn.cikian.shop.cart.entity.BusCart;
import cn.cikian.shop.cart.mapper.BusCartMapper;
import cn.cikian.shop.cart.service.BusCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-03 15:53
 */

@Service
public class BusCartServiceImpl extends ServiceImpl<BusCartMapper, BusCart> implements BusCartService {

    @Override
    public BusCart getCartByUserAndSku(String userId, String skuId) {
        LambdaQueryWrapper<BusCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusCart::getUserId, userId).eq(BusCart::getSkuId, skuId);
        List<BusCart> list = this.list(lqw);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void updateCartById(BusCart cart) {
        this.updateById(cart);
    }
}





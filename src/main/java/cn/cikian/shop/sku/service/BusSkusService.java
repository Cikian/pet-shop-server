package cn.cikian.shop.sku.service;

import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.entity.vo.AddSkuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */
public interface BusSkusService extends IService<BusSku> {
    List<AddSkuVo> findByProductId(String productId);

}

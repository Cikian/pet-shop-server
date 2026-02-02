package cn.cikian.shop.service;

import cn.cikian.shop.entity.SpecKeys;
import cn.cikian.shop.entity.vo.AddSpecVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */
public interface SpecKeysService extends IService<SpecKeys> {
    List<AddSpecVo> findByProductId(String productId);
}

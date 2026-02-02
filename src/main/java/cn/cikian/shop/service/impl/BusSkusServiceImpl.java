package cn.cikian.shop.service.impl;

import cn.cikian.shop.entity.BusSku;
import cn.cikian.shop.entity.SkuSpec;
import cn.cikian.shop.entity.vo.AddSkuVo;
import cn.cikian.shop.mapper.BusSkusMapper;
import cn.cikian.shop.mapper.SkuSpecsMapper;
import cn.cikian.shop.service.BusSkusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */
@Service
public class BusSkusServiceImpl extends ServiceImpl<BusSkusMapper, BusSku> implements BusSkusService {
    @Autowired
    private SkuSpecsMapper skuSpecsMapper;

    @Override
    public List<AddSkuVo> findByProductId(String productId) {
        LambdaQueryWrapper<BusSku> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusSku::getProductId, productId);
        List<BusSku> skus = list(lqw);
        List<String> skuIds = skus.stream().map(BusSku::getId).toList();
        if (skuIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SkuSpec> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(SkuSpec::getSkuId, skuIds);
        List<SkuSpec> skuSpecs = skuSpecsMapper.selectList(lqw2);

        List<AddSkuVo> resList = new ArrayList<>();
        for (BusSku sku : skus) {
            AddSkuVo addSkuVo = new AddSkuVo();
            BeanUtils.copyProperties(sku, addSkuVo);
            addSkuVo.setSkuSpecs(skuSpecs.stream().filter(s -> s.getSkuId().equals(sku.getId())).toList());
            resList.add(addSkuVo);
        }

        return resList;
    }
}





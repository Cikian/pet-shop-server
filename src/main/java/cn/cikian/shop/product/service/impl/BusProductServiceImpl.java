package cn.cikian.shop.product.service.impl;

import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.mapper.BusTagsMapper;
import cn.cikian.shop.category.service.BusTagsService;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.product.entity.vo.HomeProductVo;
import cn.cikian.shop.product.mapper.BusProductMapper;
import cn.cikian.shop.product.service.BusProductService;
import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.mapper.BusSkusMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class BusProductServiceImpl extends ServiceImpl<BusProductMapper, BusProduct> implements BusProductService {
    @Autowired
    private BusTagsMapper tagsMapper;
    @Autowired
    private BusTagsService tagsService;
    @Autowired
    private BusSkusMapper skuMapper;

    @Override
    public List<HomeProductVo> buildHomeRecommendList(List<BusProduct> products) {
        List<HomeProductVo> resultList = new ArrayList<>();

        List<String> productIds = products.stream().map(BusProduct::getId).toList();
        LambdaQueryWrapper<BusSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.in(BusSku::getProductId, productIds);
        List<BusSku> skus = skuMapper.selectList(skuWrapper);

        List<ProductTag> tagsByProduct = tagsService.getTagsByProduct(productIds);

        for (BusProduct product : products) {
            String productId = product.getId();
            List<BusSku> skuList = skus.stream().filter(sku -> sku.getProductId().equals(productId)).toList();
            HomeProductVo homeProductVo = HomeProductVo.create(product, skuList, tagsByProduct);
            if (homeProductVo != null) {
                resultList.add(homeProductVo);
            }
        }

        return resultList;
    }
}





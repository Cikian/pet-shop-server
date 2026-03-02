package cn.cikian.shop.product.service.impl;

import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.mapper.BusTagsMapper;
import cn.cikian.shop.category.service.BusTagsService;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.product.entity.ProductImg;
import cn.cikian.shop.product.entity.vo.HomeProductVo;
import cn.cikian.shop.product.entity.vo.Product4Detail;
import cn.cikian.shop.product.entity.vo.SkuVo;
import cn.cikian.shop.product.entity.vo.SpecVo;
import cn.cikian.shop.product.mapper.BusProductMapper;
import cn.cikian.shop.product.mapper.ProductImgMapper;
import cn.cikian.shop.product.service.BusProductService;
import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.entity.SkuSpec;
import cn.cikian.shop.sku.mapper.BusSkusMapper;
import cn.cikian.shop.sku.mapper.SkuSpecsMapper;
import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.mapper.SpecKeysMapper;
import cn.cikian.shop.spec.mapper.SpecValuesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private SkuSpecsMapper skuSpecsMapper;
    @Autowired
    private SpecKeysMapper specKeysMapper;
    @Autowired
    private SpecValuesMapper specValuesMapper;
    @Autowired
    private ProductImgMapper productImgMapper;

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

    @Override
    public Product4Detail getProductDetail(BusProduct product) {
        String productId = product.getId();
        // 获取商品的所有sku
        List<BusSku> skus = skuMapper.queryByProductId(productId);
        if (skus == null || skus.isEmpty()) {
            return null;
        }
        List<SpecKeys> specKeys = specKeysMapper.queryByProductId(productId);
        if (specKeys == null || specKeys.isEmpty()) {
            return null;
        }

        // 查询所有specValues
        List<String> specKeyIds = specKeys.stream().map(SpecKeys::getId).toList();
        LambdaQueryWrapper<SpecValues> svLqw = new LambdaQueryWrapper<>();
        svLqw.in(SpecValues::getSpecKeyId, specKeyIds);
        List<SpecValues> specValues = specValuesMapper.selectList(svLqw);

        // 查询所有skuSpec
        List<String> skuIds = skus.stream().map(BusSku::getId).toList();
        LambdaQueryWrapper<SkuSpec> ssLqw = new LambdaQueryWrapper<>();
        ssLqw.in(SkuSpec::getSkuId, skuIds);
        List<SkuSpec> skuSpecs = skuSpecsMapper.selectList(ssLqw);

        List<ProductTag> tags = tagsService.getTagsByProduct(productId);

        Product4Detail detail = new Product4Detail();
        BeanUtils.copyProperties(product, detail);
        detail.setTags(tags.stream().map(ProductTag::getTagName).collect(Collectors.toList()));

        this.buildSpecs(detail, specKeys, specValues);
        this.buildSku(detail, skus, skuSpecs, specKeys, specValues);

        List<ProductImg> productImgs = productImgMapper.selectList(new LambdaQueryWrapper<ProductImg>().eq(ProductImg::getProductId, productId));
        detail.setImages(productImgs.stream().map(ProductImg::getImgUrl).collect(Collectors.toList()));

        return detail;
    }

    private void buildSpecs(Product4Detail detail, List<SpecKeys> specKeys, List<SpecValues> specValues) {
        List<SpecVo> specVoList = new ArrayList<>();
        for (SpecKeys specKey : specKeys) {
            SpecVo specVo = new SpecVo();
            specVo.setName(specKey.getName());
            List<SpecValues> sValues = specValues.stream().filter(specValue -> specValue.getSpecKeyId().equals(specKey.getId())).toList();
            specVo.setValues(sValues.stream().map(SpecValues::getValue).toList());
            specVoList.add(specVo);
        }
        detail.setSpecs(specVoList);
    }

    private void buildSku(Product4Detail detail, List<BusSku> skus, List<SkuSpec> skuSpecs, List<SpecKeys> specKeys, List<SpecValues> specValues) {
        // 将specKeys转为map，k为id，v为name，使用stream
        Map<String, String> specKeyMap = specKeys.stream().collect(Collectors.toMap(SpecKeys::getId, SpecKeys::getName));
        Map<String, String> specValueMap = specValues.stream().collect(Collectors.toMap(SpecValues::getId, SpecValues::getValue));
        List<SkuVo> skuVoList = new ArrayList<>();

        int maxStock = 0;
        BigDecimal minPrice = new BigDecimal(99999999);
        BigDecimal maxPrice = new BigDecimal(0);

        for (BusSku sku : skus) {
            if (sku.getStock() > maxStock) {
                maxStock = sku.getStock();
            }
            if (sku.getPrice().compareTo(minPrice) < 0) {
                minPrice = sku.getPrice();
            }
            if (sku.getPrice().compareTo(maxPrice) > 0) {
                maxPrice = sku.getPrice();
            }

            SkuVo skuVo = new SkuVo();
            BeanUtils.copyProperties(sku, skuVo);
            List<SkuSpec> skuSpecList = skuSpecs.stream().filter(skuSpec -> skuSpec.getSkuId().equals(sku.getId())).toList();

            List<Map<String, String>> specs = new ArrayList<>();
            for (SkuSpec skuSpec : skuSpecList) {
                String specKeyId = skuSpec.getSpecKeyId();
                String specValueId = skuSpec.getSpecValueId();
                String specKeyName = specKeyMap.get(specKeyId);
                String specValueName = specValueMap.get(specValueId);

                Map<String, String> specMap = new HashMap<>();
                specMap.put("key", specKeyName);
                specMap.put("value", specValueName);
                specs.add(specMap);
            }
            skuVo.setSpecs(specs);
            skuVoList.add(skuVo);
        }

        detail.setStock(maxStock);
        detail.setPrice(minPrice);
        detail.setOriginalPrice(maxPrice);
        detail.setSkus(skuVoList);
    }
}





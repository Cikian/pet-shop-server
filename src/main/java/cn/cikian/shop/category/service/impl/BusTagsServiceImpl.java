package cn.cikian.shop.category.service.impl;

import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.mapper.BusTagsMapper;
import cn.cikian.shop.category.mapper.ProductTagMapper;
import cn.cikian.shop.category.service.BusTagsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-03 17:33
 */

@Service
public class BusTagsServiceImpl extends ServiceImpl<BusTagsMapper, BusTags> implements BusTagsService {
    @Autowired
    private BusTagsMapper busTagsMapper;
    @Autowired
    private ProductTagMapper productTagMapper;

    @Override
    public List<ProductTag> getTagsByProduct(List<String> productIds) {
        LambdaQueryWrapper<ProductTag> pTagLqw = new LambdaQueryWrapper<>();
        pTagLqw.in(ProductTag::getProductId, productIds);
        List<ProductTag> productTags = productTagMapper.selectList(pTagLqw);

        if (productTags.isEmpty()) {
            return List.of();
        }

        List<String> tagIds = productTags.stream().map(ProductTag::getTagId).toList();

        LambdaQueryWrapper<BusTags> bTagLqw = new LambdaQueryWrapper<>();
        bTagLqw.in(BusTags::getId, tagIds);
        List<BusTags> busTags = busTagsMapper.selectList(bTagLqw);
        if (busTags.isEmpty()) {
            return List.of();
        }

        // 转为map，tagId为K，tagName为V，使用stream处理
        Map<String, String> tagMap = busTags.stream()
                .collect(Collectors.toMap(
                        BusTags::getId,
                        BusTags::getTagName,
                        (existing, replacement) -> existing
                ));

        for (ProductTag productTag : productTags) {
            productTag.setTagName(tagMap.get(productTag.getTagId()));
        }

        return productTags;
    }

    @Override
    public List<ProductTag> getTagsByProduct(String productId) {
        if (StringUtils.isBlank(productId)) {
            return List.of();
        }
        return getTagsByProduct(Collections.singletonList(productId));
    }
}





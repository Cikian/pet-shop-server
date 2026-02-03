package cn.cikian.shop.spec.service.impl;

import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.entity.vo.AddSpecVo;
import cn.cikian.shop.spec.mapper.SpecKeysMapper;
import cn.cikian.shop.spec.mapper.SpecValuesMapper;
import cn.cikian.shop.spec.service.SpecKeysService;
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
public class SpecKeysServiceImpl extends ServiceImpl<SpecKeysMapper, SpecKeys> implements SpecKeysService {
    @Autowired
    private SpecKeysMapper specKeysMapper;
    @Autowired
    private SpecValuesMapper specValuesMapper;

    @Override
    public List<AddSpecVo> findByProductId(String productId) {
        LambdaQueryWrapper<SpecKeys> lqwKey = new LambdaQueryWrapper<>();
        lqwKey.eq(SpecKeys::getProductId, productId);
        lqwKey.orderByAsc(SpecKeys::getSortOrder);
        List<SpecKeys> specKeysList = specKeysMapper.selectList(lqwKey);
        List<String> keyIds = specKeysList.stream().map(SpecKeys::getId).toList();
        if (keyIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<SpecValues> lqwValue = new LambdaQueryWrapper<>();
        lqwValue.in(SpecValues::getSpecKeyId, keyIds);
        lqwValue.orderByAsc(SpecValues::getSortOrder);
        List<SpecValues> specValuesList = specValuesMapper.selectList(lqwValue);

        List<AddSpecVo> resList = new ArrayList<>();
        for (SpecKeys specKeys : specKeysList) {
            AddSpecVo addSpecVo = new AddSpecVo();
            addSpecVo.setId(specKeys.getId());
            addSpecVo.setName(specKeys.getName());
            addSpecVo.setProductId(specKeys.getProductId());
            addSpecVo.setInputType(specKeys.getInputType());
            addSpecVo.setSortOrder(specKeys.getSortOrder());
            addSpecVo.setSpecValueList(specValuesList.stream().filter(specValues -> specValues.getSpecKeyId().equals(specKeys.getId())).toList());
            resList.add(addSpecVo);
        }

        return resList;
    }
}





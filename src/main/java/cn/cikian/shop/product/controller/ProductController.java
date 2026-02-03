package cn.cikian.shop.product.controller;


import cn.cikian.shop.product.entity.ProductImg;
import cn.cikian.shop.product.entity.vo.AddProductVo;
import cn.cikian.shop.product.service.BusProductService;
import cn.cikian.shop.product.service.ProductImgService;
import cn.cikian.shop.sku.entity.vo.AddSkuVo;
import cn.cikian.shop.sku.service.BusSkusService;
import cn.cikian.shop.sku.service.SkuSpecsService;
import cn.cikian.shop.spec.entity.vo.AddSpecVo;
import cn.cikian.shop.product.entity.vo.ProductImgVo;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.entity.SkuSpec;
import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.service.SpecKeysService;
import cn.cikian.shop.spec.service.SpecValuesService;
import cn.cikian.system.core.utils.OssUtils;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upyun.UpException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
 * @since 2026-01-31 02:32
 */

@RestController
@RequestMapping("/api/goods")
public class ProductController {
    @Autowired
    private BusProductService productService;
    @Autowired
    private ProductImgService imgService;
    @Autowired
    private SpecKeysService specKeysService;
    @Autowired
    private SpecValuesService specValuesService;
    @Autowired
    private BusSkusService skusService;
    @Autowired
    private SkuSpecsService skuSpecsService;
    @Autowired
    private OssUtils ossUtils;

    @GetMapping(value = "/list")
    public Result<IPage<BusProduct>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        LambdaQueryWrapper<BusProduct> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<BusProduct> page = new Page<>(pageNo, pageSize);
        IPage<BusProduct> pageList = productService.page(page, lqw);
        return Result.OK(pageList);
    }

    @GetMapping("/{id}")
    public Result<BusProduct> query(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    @Transactional
    @PostMapping
    public Result<?> add(AddProductVo data) {
        String proId = IdWorker.getIdStr();
        data.setId(proId);
        BusProduct product = extractProduct(data);
        extractProductImgAndSave(data);
        Map<String, String> temp2IdMap = extractSpecsAndSave(data);
        extractSkuSpecsAndSave(data, temp2IdMap);
        productService.save(product);
        return Result.ok("添加成功！");
    }

    @Transactional
    @PutMapping
    public Result<?> edit(AddProductVo data) {
        BusProduct product = extractProduct(data);
        extractProductImgAndSave(data);
        // 处理规格相关数据（SpecKey、SpecValue）
        Map<String, String> temp2IdMap = handleSpecs(data);
        // 处理SKU相关数据
        handleSkus(data, temp2IdMap);
        productService.updateById(product);
        return Result.ok("编辑成功！");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusProduct> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(BusProduct::getName, map.get("name")[0]);
        }
    }

    private BusProduct extractProduct(AddProductVo data) {
        BusProduct product = new BusProduct();
        product.setId(data.getId());
        product.setName(data.getName());
        product.setDescription(data.getDescription());
        product.setCategoryId(data.getCategoryId());
        product.setStatus(data.getStatus());

        MultipartFile mainImg = data.getMainImg();
        if (mainImg != null) {
            String imgName = mainImg.getOriginalFilename();
            if (imgName != null) {
                imgName = imgName.replaceAll("\\s+", "");
            }
            try {
                byte[] bytes = mainImg.getBytes();
                String fileUrl = ossUtils.upToOss("/petShop/product/" + imgName, bytes);
                product.setMainImg(fileUrl);
            } catch (IOException | UpException e) {
                throw new RuntimeException(e);
            }
        }

        return product;
    }

    private void extractProductImgAndSave(AddProductVo data) {
        String mainId = data.getId();
        List<ProductImgVo> pictures = data.getPictures();
        List<ProductImgVo> newPics = new ArrayList<>();
        List<ProductImgVo> oldPics = new ArrayList<>();
        for (ProductImgVo vo : pictures) {
            String id = vo.getId();
            if (id == null || id.isEmpty()) {
                newPics.add(vo);
            } else {
                oldPics.add(vo);
            }
        }

        List<String> oldPicIds = oldPics.stream().map(ProductImgVo::getId).toList();
        deleteOldPics(oldPicIds, mainId);
        updateOldPics(oldPics);

        List<ProductImg> picList = saveProduct2Oss(newPics, mainId);

        if (picList != null && !picList.isEmpty()) {
            picList.forEach(imgService::save);
        }
    }

    private Map<String, String> extractSpecsAndSave(AddProductVo data) {
        String mainId = data.getId();
        List<AddSpecVo> specs = data.getSpecs();
        if (specs == null || specs.isEmpty()) {
            return null;
        }

        Map<String, String> resMap = new HashMap<>();

        List<SpecKeys> specKeysList = new ArrayList<>();
        List<SpecValues> specValuesList = new ArrayList<>();

        for (AddSpecVo spec : specs) {
            String specTempId = spec.getId();
            if (specTempId == null || specTempId.isEmpty()) {
                continue;
            }
            String specKeyId = IdWorker.getIdStr();
            resMap.put(specTempId, specKeyId);
            SpecKeys sKey = new SpecKeys();
            sKey.setId(specKeyId);
            sKey.setProductId(mainId);
            sKey.setName(spec.getName());
            sKey.setSortOrder(spec.getSortOrder());
            sKey.setInputType(spec.getInputType());
            specKeysList.add(sKey);

            List<SpecValues> specValueList = spec.getSpecValueList();
            if (specValueList == null) {
                specValueList = new ArrayList<>();
            }

            for (SpecValues specValue : specValueList) {
                String specValueTempId = specValue.getId();
                if (specValueTempId == null || specValueTempId.isEmpty()) {
                    continue;
                }
                String specValueKeyId = IdWorker.getIdStr();
                resMap.put(specValueTempId, specValueKeyId);

                specValue.setId(specValueKeyId);
                specValue.setSpecKeyId(specKeyId);
                specValuesList.add(specValue);
            }

        }

        specKeysService.saveBatch(specKeysList);
        specValuesService.saveBatch(specValuesList);

        return resMap;
    }

    private void extractSkuSpecsAndSave(AddProductVo data, Map<String, String> tempIdMap) {
        String productId = data.getId();
        List<AddSkuVo> skus = data.getSkus();
        if (skus == null || skus.isEmpty()) {
            return;
        }
        List<BusSku> skuList = new ArrayList<>();
        List<SkuSpec> skuSpecsList = new ArrayList<>();
        for (AddSkuVo sku : skus) {
            String skuId = IdWorker.getIdStr();
            BusSku busSku = new BusSku();
            BeanUtils.copyProperties(sku, busSku);
            busSku.setId(skuId);
            busSku.setProductId(productId);
            skuList.add(busSku);

            List<SkuSpec> skuSpecs = sku.getSkuSpecs();
            if (skuSpecs == null) {
                skuSpecs = new ArrayList<>();
            }
            for (SkuSpec skuSpec : skuSpecs) {
                String specKeyId = tempIdMap.get(skuSpec.getSpecKeyId());
                String specValueId = tempIdMap.get(skuSpec.getSpecValueId());
                if (specKeyId == null || specValueId == null) {
                    continue;
                }
                skuSpec.setSkuId(skuId);
                skuSpec.setSpecKeyId(specKeyId);
                skuSpec.setSpecValueId(specValueId);
                skuSpecsList.add(skuSpec);
            }
        }

        skusService.saveBatch(skuList);
        skuSpecsService.saveBatch(skuSpecsList);
    }


    private List<ProductImg> saveProduct2Oss(List<ProductImgVo> pictures, String mainId) {
        if (pictures == null) {
            return null;
        }
        List<ProductImg> picList = new ArrayList<>();
        for (ProductImgVo img : pictures) {
            ProductImg pic = new ProductImg();
            pic.setProductId(mainId);

            MultipartFile imgFile = img.getImg();
            if (imgFile == null) {
                continue;
            }
            String imgName = imgFile.getOriginalFilename();
            imgName = imgName.replaceAll("\\s+", "");

            try {
                byte[] bytes = imgFile.getBytes();
                String fileUrl = ossUtils.upToOss("/petShop/product/" + imgName, bytes);
                pic.setImgUrl(fileUrl);
                pic.setSortOrder(img.getSortOrder());
                pic.setDescription(img.getDescription());
                picList.add(pic);
            } catch (IOException | UpException e) {
                throw new RuntimeException(e);
            }
        }

        return picList;
    }

    private void deleteOldPics(List<String> currentIds, String mainId) {
        if (currentIds == null || currentIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<ProductImg> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductImg::getProductId, mainId);
        lqw.notIn(ProductImg::getId, currentIds);
        imgService.remove(lqw);

        // todo:删除OSS中的图片
    }

    private void updateOldPics(List<ProductImgVo> pictures) {
        if (pictures == null || pictures.isEmpty()) {
            return;
        }
        List<ProductImg> pics = new ArrayList<>();
        for (ProductImgVo vo : pictures) {
            String id = vo.getId();
            if (id == null || id.isEmpty()) {
                continue;
            }
            ProductImg pic = new ProductImg();
            pic.setId(id);
            pic.setDescription(vo.getDescription());
            pic.setSortOrder(vo.getSortOrder());
            pics.add(pic);
        }

        if (!pics.isEmpty()) {
            pics.forEach(imgService::updateById);
        }
    }

    private Map<String, String> handleSpecs(AddProductVo data) {
        String mainId = data.getId();
        List<AddSpecVo> specs = data.getSpecs();
        if (specs == null || specs.isEmpty()) {
            // 如果没有规格数据，删除所有现有规格
            LambdaQueryWrapper<SpecKeys> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SpecKeys::getProductId, mainId);
            List<SpecKeys> specKeys = specKeysService.list(lqw);
            List<String> specKeyIds = specKeys.stream().map(SpecKeys::getId).toList();
            
            if (!specKeyIds.isEmpty()) {
                LambdaQueryWrapper<SpecValues> lqw2 = new LambdaQueryWrapper<>();
                lqw2.in(SpecValues::getSpecKeyId, specKeyIds);
                specValuesService.remove(lqw2);
            }
            specKeysService.remove(lqw);
            return null;
        }

        Map<String, String> resMap = new HashMap<>();
        
        // 获取现有规格ID列表
        LambdaQueryWrapper<SpecKeys> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SpecKeys::getProductId, mainId);
        List<SpecKeys> existingSpecKeys = specKeysService.list(lqw);
        Map<String, SpecKeys> existingSpecKeyMap = existingSpecKeys.stream()
                .collect(Collectors.toMap(SpecKeys::getId, specKey -> specKey));
        
        // 收集所有提交的规格ID（排除临时ID）
        List<String> submittedSpecIds = specs.stream()
                .map(AddSpecVo::getId)
                .filter(id -> id != null && !id.startsWith("temp_id"))
                .collect(Collectors.toList());
        
        // 删除不存在于提交参数中的规格
        List<String> specsToDelete = existingSpecKeyMap.keySet().stream()
                .filter(id -> !submittedSpecIds.contains(id))
                .collect(Collectors.toList());
        
        if (!specsToDelete.isEmpty()) {
            // 删除关联的规格值
            LambdaQueryWrapper<SpecValues> valueLqw = new LambdaQueryWrapper<>();
            valueLqw.in(SpecValues::getSpecKeyId, specsToDelete);
            specValuesService.remove(valueLqw);
            
            // 删除规格
            specKeysService.removeByIds(specsToDelete);
        }

        List<SpecKeys> specKeysToSave = new ArrayList<>();
        List<SpecKeys> specKeysToUpdate = new ArrayList<>();
        List<SpecValues> specValuesToSave = new ArrayList<>();
        List<SpecValues> specValuesToUpdate = new ArrayList<>();

        for (AddSpecVo spec : specs) {
            String specId = spec.getId();
            
            if (specId == null || specId.startsWith("temp_id")) {
                // 新增规格
                String newSpecKeyId = IdWorker.getIdStr();
                if (specId != null) {
                    resMap.put(specId, newSpecKeyId);
                }
                SpecKeys sKey = new SpecKeys();
                sKey.setId(newSpecKeyId);
                sKey.setProductId(mainId);
                sKey.setName(spec.getName());
                sKey.setSortOrder(spec.getSortOrder());
                sKey.setInputType(spec.getInputType());
                specKeysToSave.add(sKey);
                
                // 处理规格值
                handleSpecValues(spec, newSpecKeyId, specValuesToSave, resMap);
            } else {
                // 更新规格
                SpecKeys existingSpecKey = existingSpecKeyMap.get(specId);
                if (existingSpecKey != null) {
                    existingSpecKey.setName(spec.getName());
                    existingSpecKey.setSortOrder(spec.getSortOrder());
                    existingSpecKey.setInputType(spec.getInputType());
                    specKeysToUpdate.add(existingSpecKey);
                    
                    // 处理规格值
                    handleSpecValuesForUpdate(spec, specId, specValuesToSave, specValuesToUpdate, resMap);
                }
            }
        }

        // 执行保存和更新操作
        if (!specKeysToSave.isEmpty()) {
            specKeysService.saveBatch(specKeysToSave);
        }
        if (!specKeysToUpdate.isEmpty()) {
            specKeysService.updateBatchById(specKeysToUpdate);
        }
        if (!specValuesToSave.isEmpty()) {
            specValuesService.saveBatch(specValuesToSave);
        }
        if (!specValuesToUpdate.isEmpty()) {
            specValuesService.updateBatchById(specValuesToUpdate);
        }

        return resMap;
    }

    private void handleSpecValues(AddSpecVo spec, String specKeyId, List<SpecValues> specValuesToSave, Map<String, String> resMap) {
        List<SpecValues> specValueList = spec.getSpecValueList();
        if (specValueList == null) {
            return;
        }

        for (SpecValues specValue : specValueList) {
            String specValueTempId = specValue.getId();
            if (specValueTempId == null || specValueTempId.isEmpty()) {
                continue;
            }
            String specValueKeyId = IdWorker.getIdStr();
            if (specValueTempId.startsWith("temp_id")) {
                resMap.put(specValueTempId, specValueKeyId);
            }

            specValue.setId(specValueKeyId);
            specValue.setSpecKeyId(specKeyId);
            specValuesToSave.add(specValue);
        }
    }

    private void handleSpecValuesForUpdate(AddSpecVo spec, String specKeyId, List<SpecValues> specValuesToSave, 
                                          List<SpecValues> specValuesToUpdate, Map<String, String> resMap) {
        List<SpecValues> specValueList = spec.getSpecValueList();
        if (specValueList == null) {
            // 如果没有规格值，删除所有现有规格值
            LambdaQueryWrapper<SpecValues> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SpecValues::getSpecKeyId, specKeyId);
            specValuesService.remove(lqw);
            return;
        }

        // 获取现有规格值
        LambdaQueryWrapper<SpecValues> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SpecValues::getSpecKeyId, specKeyId);
        List<SpecValues> existingValues = specValuesService.list(lqw);
        Map<String, SpecValues> existingValueMap = existingValues.stream()
                .collect(Collectors.toMap(SpecValues::getId, value -> value));
        
        // 收集所有提交的规格值ID（排除临时ID）
        List<String> submittedValueIds = specValueList.stream()
                .map(SpecValues::getId)
                .filter(id -> id != null && !id.startsWith("temp_id"))
                .collect(Collectors.toList());
        
        // 删除不存在于提交参数中的规格值
        List<String> valuesToDelete = existingValueMap.keySet().stream()
                .filter(id -> !submittedValueIds.contains(id))
                .collect(Collectors.toList());
        
        if (!valuesToDelete.isEmpty()) {
            specValuesService.removeByIds(valuesToDelete);
        }

        for (SpecValues specValue : specValueList) {
            String specValueId = specValue.getId();
            
            if (specValueId == null || specValueId.startsWith("temp_id")) {
                // 新增规格值
                String newSpecValueId = IdWorker.getIdStr();
                if (specValueId != null) {
                    resMap.put(specValueId, newSpecValueId);
                }
                specValue.setId(newSpecValueId);
                specValue.setSpecKeyId(specKeyId);
                specValuesToSave.add(specValue);
            } else {
                // 更新规格值
                SpecValues existingValue = existingValueMap.get(specValueId);
                if (existingValue != null) {
                    existingValue.setValue(specValue.getValue());
                    existingValue.setImage(specValue.getImage());
                    existingValue.setSortOrder(specValue.getSortOrder());
                    specValuesToUpdate.add(existingValue);
                }
            }
        }
    }

    private void handleSkus(AddProductVo data, Map<String, String> temp2IdMap) {
        String productId = data.getId();
        List<AddSkuVo> skus = data.getSkus();
        
        if (skus == null || skus.isEmpty()) {
            // 如果没有SKU数据，删除所有现有SKU及关联数据
            LambdaQueryWrapper<BusSku> skuLqw = new LambdaQueryWrapper<>();
            skuLqw.eq(BusSku::getProductId, productId);
            List<BusSku> existingSkus = skusService.list(skuLqw);
            List<String> existingSkuIds = existingSkus.stream().map(BusSku::getId).toList();
            
            if (!existingSkuIds.isEmpty()) {
                LambdaQueryWrapper<SkuSpec> specLqw = new LambdaQueryWrapper<>();
                specLqw.in(SkuSpec::getSkuId, existingSkuIds);
                skuSpecsService.remove(specLqw);
            }
            skusService.remove(skuLqw);
            return;
        }

        // 获取现有SKU
        LambdaQueryWrapper<BusSku> skuLqw = new LambdaQueryWrapper<>();
        skuLqw.eq(BusSku::getProductId, productId);
        List<BusSku> existingSkus = skusService.list(skuLqw);
        Map<String, BusSku> existingSkuMap = existingSkus.stream()
                .collect(Collectors.toMap(BusSku::getId, sku -> sku));
        
        // 收集所有提交的SKU ID（排除临时ID）
        List<String> submittedSkuIds = skus.stream()
                .map(AddSkuVo::getId)
                .filter(id -> id != null && !id.startsWith("temp_id"))
                .collect(Collectors.toList());
        
        // 删除不存在于提交参数中的SKU
        List<String> skusToDelete = existingSkuMap.keySet().stream()
                .filter(id -> !submittedSkuIds.contains(id))
                .collect(Collectors.toList());
        
        if (!skusToDelete.isEmpty()) {
            // 删除关联的SKU规格
            LambdaQueryWrapper<SkuSpec> specLqw = new LambdaQueryWrapper<>();
            specLqw.in(SkuSpec::getSkuId, skusToDelete);
            skuSpecsService.remove(specLqw);
            
            // 删除SKU
            skusService.removeByIds(skusToDelete);
        }

        List<BusSku> skusToSave = new ArrayList<>();
        List<BusSku> skusToUpdate = new ArrayList<>();
        List<SkuSpec> skuSpecsToSave = new ArrayList<>();

        for (AddSkuVo sku : skus) {
            String skuId = sku.getId();
            
            if (skuId == null || skuId.startsWith("temp_id")) {
                // 新增SKU
                String newSkuId = IdWorker.getIdStr();
                BusSku busSku = new BusSku();
                BeanUtils.copyProperties(sku, busSku);
                busSku.setId(newSkuId);
                busSku.setProductId(productId);
                skusToSave.add(busSku);
                
                // 处理SKU规格
                handleSkuSpecs(sku, newSkuId, temp2IdMap, skuSpecsToSave);
            } else {
                // 更新SKU
                BusSku existingSku = existingSkuMap.get(skuId);
                if (existingSku != null) {
                    BeanUtils.copyProperties(sku, existingSku, "id", "productId");
                    skusToUpdate.add(existingSku);
                    
                    // 删除旧的SKU规格并添加新的
                    LambdaQueryWrapper<SkuSpec> specLqw = new LambdaQueryWrapper<>();
                    specLqw.eq(SkuSpec::getSkuId, skuId);
                    skuSpecsService.remove(specLqw);
                    
                    // 处理新的SKU规格
                    handleSkuSpecs(sku, skuId, temp2IdMap, skuSpecsToSave);
                }
            }
        }

        // 执行保存和更新操作
        if (!skusToSave.isEmpty()) {
            skusService.saveBatch(skusToSave);
        }
        if (!skusToUpdate.isEmpty()) {
            skusService.updateBatchById(skusToUpdate);
        }
        if (!skuSpecsToSave.isEmpty()) {
            skuSpecsService.saveBatch(skuSpecsToSave);
        }
    }

    private void handleSkuSpecs(AddSkuVo sku, String skuId, Map<String, String> temp2IdMap, List<SkuSpec> skuSpecsToSave) {
        List<SkuSpec> skuSpecs = sku.getSkuSpecs();
        if (skuSpecs == null) {
            return;
        }
        
        for (SkuSpec skuSpec : skuSpecs) {
            String specKeyId = skuSpec.getSpecKeyId();
            String specValueId = skuSpec.getSpecValueId();
            
            // 替换临时ID为真实ID
            if (temp2IdMap != null) {
                if (specKeyId != null && specKeyId.startsWith("temp_id")) {
                    specKeyId = temp2IdMap.get(specKeyId);
                }
                if (specValueId != null && specValueId.startsWith("temp_id")) {
                    specValueId = temp2IdMap.get(specValueId);
                }
            }
            
            if (specKeyId == null || specValueId == null) {
                continue;
            }
            
            SkuSpec newSkuSpec = new SkuSpec();
            newSkuSpec.setSkuId(skuId);
            newSkuSpec.setSpecKeyId(specKeyId);
            newSkuSpec.setSpecValueId(specValueId);
            skuSpecsToSave.add(newSkuSpec);
        }
    }

    private void removeExistsSkuSpecs(AddProductVo data) {
        String productId = data.getId();

        LambdaQueryWrapper<BusSku> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusSku::getProductId, productId);
        List<BusSku> skus = skusService.list(lqw);
        List<String> currentSkuIds = skus.stream().map(BusSku::getId).toList();

        List<AddSkuVo> newSkus = data.getSkus();

        List<String> requestSkuIds = newSkus.stream().map(AddSkuVo::getId).toList();
        List<String> newSkuIds = requestSkuIds.stream().filter(s -> (s == null || s.startsWith("temp_id"))).toList();
        List<String> oldSkuIds = requestSkuIds.stream().filter(s -> (s != null && !s.startsWith("temp_id"))).toList();

        if (!currentSkuIds.isEmpty()) {
            LambdaQueryWrapper<SkuSpec> lqw2 = new LambdaQueryWrapper<>();
            lqw2.in(SkuSpec::getSkuId, currentSkuIds);
            skuSpecsService.remove(lqw2);
        }
        skusService.remove(lqw);
    }

    private void removeExistsSpecs(AddProductVo data) {
        String productId = data.getId();

        LambdaQueryWrapper<SpecKeys> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SpecKeys::getProductId, productId);
        List<SpecKeys> specKeys = specKeysService.list(lqw);
        List<String> specKeyIds = specKeys.stream().map(SpecKeys::getId).toList();

        LambdaQueryWrapper<SpecValues> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(SpecValues::getSpecKeyId, specKeyIds);
        specValuesService.remove(lqw2);
        specKeysService.remove(lqw);
    }
}

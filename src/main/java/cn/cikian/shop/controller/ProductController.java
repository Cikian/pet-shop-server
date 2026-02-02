package cn.cikian.shop.controller;


import cn.cikian.shop.entity.*;
import cn.cikian.shop.entity.vo.AddProductVo;
import cn.cikian.shop.entity.vo.AddSkuVo;
import cn.cikian.shop.entity.vo.AddSpecVo;
import cn.cikian.shop.entity.vo.ProductImgVo;
import cn.cikian.shop.service.*;
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
        String productId = product.getId();
        extractProductImgAndSave(data);
        removeExistsSkuSpecs(productId);
        removeExistsSpecs(productId);

        Map<String, String> temp2IdMap = extractSpecsAndSave(data);
        extractSkuSpecsAndSave(data, temp2IdMap);
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

    private void removeExistsSkuSpecs(String productId) {
        LambdaQueryWrapper<BusSku> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusSku::getProductId, productId);
        List<BusSku> skus = skusService.list(lqw);
        List<String> skuIds = skus.stream().map(BusSku::getId).toList();

        if (!skuIds.isEmpty()) {
            LambdaQueryWrapper<SkuSpec> lqw2 = new LambdaQueryWrapper<>();
            lqw2.in(SkuSpec::getSkuId, skuIds);
            skuSpecsService.remove(lqw2);
        }
        skusService.remove(lqw);
    }

    private void removeExistsSpecs(String productId) {
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

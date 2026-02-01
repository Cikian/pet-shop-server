package cn.cikian.shop.controller;


import cn.cikian.shop.entity.BusProduct;
import cn.cikian.shop.entity.ProductImg;
import cn.cikian.shop.entity.vo.AddProductVo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    @PostMapping
    public Result<?> add(AddProductVo data) {
        String proId = IdWorker.getIdStr();
        data.setId(proId);
        BusProduct product = extractProduct(data);
        extractProductImgAndSave(data);
        productService.save(product);

        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(AddProductVo data) {
        extractProductImgAndSave(data);
        BusProduct product = extractProduct(data);
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
}

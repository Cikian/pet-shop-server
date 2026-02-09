package cn.cikian.shop.product.controller;


import cn.cikian.shop.category.entity.BusCategory;
import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.service.BusTagsService;
import cn.cikian.shop.category.service.ProductTagService;
import cn.cikian.shop.product.entity.BusProduct;
import cn.cikian.shop.product.entity.HomeSlideshow;
import cn.cikian.shop.product.entity.ProductImg;
import cn.cikian.shop.product.entity.Slideshow;
import cn.cikian.shop.product.entity.vo.AddProductVo;
import cn.cikian.shop.product.entity.vo.ProductImgVo;
import cn.cikian.shop.product.service.BusProductService;
import cn.cikian.shop.product.service.HomeSlideshowService;
import cn.cikian.shop.product.service.ProductImgService;
import cn.cikian.shop.product.service.SlideshowService;
import cn.cikian.shop.sku.entity.BusSku;
import cn.cikian.shop.sku.entity.SkuSpec;
import cn.cikian.shop.sku.entity.vo.AddSkuVo;
import cn.cikian.shop.sku.service.BusSkusService;
import cn.cikian.shop.sku.service.SkuSpecsService;
import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.entity.vo.AddSpecVo;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:32
 */

@RestController
@RequestMapping("/api/home/slide")
public class SlideshowController {
    @Autowired
    private BusProductService productService;
    @Autowired
    private HomeSlideshowService homeSlideshowService;
    @Autowired
    private SlideshowService slideshowService;
    @Autowired
    private OssUtils ossUtils;

    @GetMapping
    public Result<?> queryList() {
        LambdaQueryWrapper<HomeSlideshow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HomeSlideshow::getStatus, true);
        lqw.orderByAsc(HomeSlideshow::getSortOrder);
        List<HomeSlideshow> list = homeSlideshowService.list(lqw);
        return Result.OK(list);
    }

    @PostMapping
    public Result<?> add(
            @RequestPart("data") Slideshow slideshow, // 接收 JSON 字符串并转为对象
            @RequestPart("file") MultipartFile file      // 接收文件
    ) {
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                originalFilename = originalFilename.replaceAll("\\s+", "");
            }
            try {
                byte[] bytes = file.getBytes();
                String fileUrl = ossUtils.upToOss("/petShop/slideshow/" + originalFilename, bytes);
                slideshow.setDisplayImg(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 保存业务数据
        slideshowService.save(slideshow);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(
            @RequestPart("data") Slideshow slideshow, // 接收 JSON 字符串并转为对象
            @RequestPart(value = "file", required = false) MultipartFile file // 设置 required=false
    ) {
        // 1. 判断是否上传了新图片
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                originalFilename = originalFilename.replaceAll("\\s+", "");
            }
            try {
                byte[] bytes = file.getBytes();
                String fileUrl = ossUtils.upToOss("/petShop/slideshow/" + originalFilename, bytes);
                slideshow.setDisplayImg(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 执行更新
        slideshowService.updateById(slideshow);
        return Result.ok("编辑成功！");
    }

}

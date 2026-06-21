package cn.cikian.shop.product.controller;


import cn.cikian.oss.service.OssServiceContext;
import cn.cikian.shop.product.entity.Slideshow;
import cn.cikian.shop.product.service.BusProductService;
import cn.cikian.shop.product.service.HomeSlideshowService;
import cn.cikian.shop.product.service.SlideshowService;
import cn.cikian.system.sys.entity.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:32
 */

@Slf4j
@RestController
@RequestMapping("/api/home/slide")
@Tag(name = "首页轮播图", description = "首页轮播图相关接口")
public class SlideshowController {
    @Autowired
    private BusProductService productService;
    @Autowired
    private HomeSlideshowService homeSlideshowService;
    @Autowired
    private SlideshowService slideshowService;
    @Autowired
    private OssServiceContext ossUtils;

    @Operation(summary = "添加首页轮播图", description = "添加新的首页轮播图")
    @PostMapping
    public Result<?> add(
            @RequestPart("data") Slideshow slideshow, // 接收 JSON 字符串并转为对象
            @RequestPart(value = "file", required = false) MultipartFile file      // 接收文件
    ) {
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                originalFilename = originalFilename.replaceAll("\\s+", "");
            }
            try {
                byte[] bytes = file.getBytes();
                URL fileUrl = ossUtils.putObject("/petShop/slideshow/" + originalFilename, bytes);
                slideshow.setDisplayImg(fileUrl.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 保存业务数据
        slideshowService.save(slideshow);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "删除首页轮播图", description = "根据ID删除首页轮播图信息")
    @PutMapping
    public Result<?> delete(
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
                URL fileUrl = ossUtils.putObject("/petShop/slideshow/" + originalFilename, bytes);
                slideshow.setDisplayImg(fileUrl.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 执行更新
        slideshowService.updateById(slideshow);
        return Result.ok("编辑成功！");
    }

    @Operation(summary = "编辑首页轮播图", description = "根据ID编辑首页轮播图信息")
    @DeleteMapping("/{id}")
    public Result<?> edit(@PathVariable String id) {
        slideshowService.removeById(id);
        return Result.ok("编辑成功！");
    }

}

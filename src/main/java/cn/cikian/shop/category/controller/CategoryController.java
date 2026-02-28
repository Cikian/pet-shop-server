package cn.cikian.shop.category.controller;


import cn.cikian.shop.category.entity.BusCategory;
import cn.cikian.shop.category.service.BusCategoryService;
import cn.cikian.system.core.utils.OssUtils;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:32
 */

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cate")
@Tag(name = "分类", description = "分类相关接口")
public class CategoryController {
    @Autowired
    private BusCategoryService cateService;
    @Autowired
    private OssUtils ossUtils;

    @Operation(summary = "查询分类分页列表", description = "根据分页参数和查询条件查询分类分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<BusCategory>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        LambdaQueryWrapper<BusCategory> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<BusCategory> page = new Page<>(pageNo, pageSize);
        IPage<BusCategory> pageList = cateService.page(page, lqw);
        return Result.OK(pageList);
    }

    @Operation(summary = "根据ID查询分类", description = "根据分类ID查询分类详情")
    @GetMapping("/{id}")
    public Result<BusCategory> query(@PathVariable Long id) {
        return Result.ok(cateService.getById(id));
    }

    @Operation(summary = "添加分类", description = "添加新的分类")
    @PostMapping(value = "/add")
    public Result<?> add(
            @RequestPart("category") BusCategory category, // 接收 JSON 字符串并转为对象
            @RequestPart("file") MultipartFile file      // 接收文件
    ) {
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                originalFilename = originalFilename.replaceAll("\\s+", "");
            }
            try {
                byte[] bytes = file.getBytes();
                String fileUrl = ossUtils.upToOss("/petShop/category/" + originalFilename, bytes);
                category.setImgUrl(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 保存业务数据
        cateService.save(category);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "编辑分类", description = "根据ID编辑分类信息")
    @PutMapping(value = "/edit")
    public Result<?> edit(
            @RequestPart("category") BusCategory category,
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
                String fileUrl = ossUtils.upToOss("/petShop/category/" + originalFilename, bytes);
                category.setImgUrl(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 执行更新
        cateService.updateById(category);
        return Result.ok("编辑成功！");
    }

    @Operation(summary = "删除分类", description = "根据ID删除分类")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        cateService.removeById(id);
        return Result.ok("删除成功！");
    }

    @Operation(summary = "更新分类是否在首页展示", description = "根据ID更新分类是否在首页展示")
    @GetMapping("/onhome")
    public Result<String> changeOnHomeStatus(@RequestParam String id, @RequestParam Boolean currentStatus) {
        Boolean onHome = !currentStatus;
        BusCategory category = cateService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        category.setOnHome(onHome);
        cateService.updateById(category);
        return Result.ok("更新成功");
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusCategory> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(BusCategory::getName, map.get("name")[0]);
        }
    }
}

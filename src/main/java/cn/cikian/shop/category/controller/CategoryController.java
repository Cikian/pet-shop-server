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

import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:32
 */

@RestController
@RequestMapping("/api/cate")
public class CategoryController {
    @Autowired
    private BusCategoryService cateService;
    @Autowired
    private OssUtils ossUtils;

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

    @GetMapping("/{id}")
    public Result<BusCategory> query(@PathVariable Long id) {
        return Result.ok(cateService.getById(id));
    }

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

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        cateService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusCategory> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(BusCategory::getName, map.get("name")[0]);
        }
    }
}

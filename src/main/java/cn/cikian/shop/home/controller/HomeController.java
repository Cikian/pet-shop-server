package cn.cikian.shop.home.controller;


import cn.cikian.shop.category.entity.BusCategory;
import cn.cikian.shop.category.service.BusCategoryService;
import cn.cikian.shop.product.entity.HomeSlideshow;
import cn.cikian.shop.product.service.HomeSlideshowService;
import cn.cikian.system.core.utils.OssUtils;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/home")
@Tag(name = "首页", description = "首页相关接口")
public class HomeController {
    @Autowired
    private BusCategoryService cateService;
    @Autowired
    private HomeSlideshowService homeSlideshowService;

    @Operation(summary = "查询首页分类分页列表", description = "根据分页参数和查询条件查询首页分类分页列表")
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

    @Operation(summary = "查询首页展示的分类列表", description = "查询所有在首页显示的分类")
    @GetMapping("/home")
    public Result<List<BusCategory>> getHomeCate() {
        LambdaQueryWrapper<BusCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusCategory::getOnHome, true);
        lqw.eq(BusCategory::getStatus, 1);
        List<BusCategory> list = cateService.list(lqw);
        return Result.ok(list);
    }

    @Operation(summary = "查询首页轮播图列表", description = "查询所有在首页显示的轮播图")
    @GetMapping("/slide")
    public Result<?> queryList() {
        LambdaQueryWrapper<HomeSlideshow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HomeSlideshow::getStatus, true);
        lqw.orderByAsc(HomeSlideshow::getSortOrder);
        List<HomeSlideshow> list = homeSlideshowService.list(lqw);
        return Result.OK(list);
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusCategory> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(BusCategory::getName, map.get("name")[0]);
        }
    }
}

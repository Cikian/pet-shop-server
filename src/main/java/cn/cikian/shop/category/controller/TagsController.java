package cn.cikian.shop.category.controller;


import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.service.BusTagsService;
import cn.cikian.shop.category.service.ProductTagService;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/tags")
public class TagsController {
    @Autowired
    private BusTagsService tagsService;
    @Autowired
    private ProductTagService pTagService;


    @GetMapping(value = "/list")
    public Result<IPage<BusTags>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        LambdaQueryWrapper<BusTags> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<BusTags> page = new Page<>(pageNo, pageSize);
        IPage<BusTags> pageList = tagsService.page(page, lqw);
        return Result.OK(pageList);
    }

    @GetMapping(value = "/get")
    public Result<List<BusTags>> queryByKeyWord(@RequestParam(name = "keyword", required = false) String keyword) {
        LambdaQueryWrapper<BusTags> lqw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(BusTags::getTagName, keyword);
        }
        List<BusTags> list = tagsService.list(lqw);
        return Result.OK(list);
    }

    @GetMapping("/{id}")
    public Result<BusTags> query(@PathVariable String id) {
        return Result.ok(tagsService.getById(id));
    }

    @GetMapping
    public Result<List<BusTags>> queryByProductId(@RequestParam String productId) {
        LambdaQueryWrapper<ProductTag> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductTag::getProductId, productId);
        List<ProductTag> list = pTagService.list(lqw);

        List<String> tagIds = list.stream().map(ProductTag::getTagId).toList();

        return Result.ok(tagsService.listByIds(tagIds));
    }

    @PostMapping
    public Result<?> add(@RequestBody BusTags product) {
        tagsService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody BusTags checkForm) {
        tagsService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagsService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusTags> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(BusTags::getTagName, map.get("name")[0]);
        }
    }
}

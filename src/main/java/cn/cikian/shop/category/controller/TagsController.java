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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tags")
@Tag(name = "标签", description = "标签相关接口")
public class TagsController {
    @Autowired
    private BusTagsService tagsService;
    @Autowired
    private ProductTagService pTagService;


    @Operation(summary = "查询标签分页列表", description = "根据分页参数和查询条件查询标签分页列表")
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

    @Operation(summary = "根据关键词查询标签", description = "根据标签名称关键词查询标签列表")
    @GetMapping(value = "/get")
    public Result<List<BusTags>> queryByKeyWord(@RequestParam(name = "keyword", required = false) String keyword) {
        LambdaQueryWrapper<BusTags> lqw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(BusTags::getTagName, keyword);
        }
        List<BusTags> list = tagsService.list(lqw);
        return Result.OK(list);
    }

    @Operation(summary = "根据ID查询标签", description = "根据标签ID查询标签详情")
    @GetMapping("/{id}")
    public Result<BusTags> query(@PathVariable String id) {
        return Result.ok(tagsService.getById(id));
    }

    @Operation(summary = "根据产品ID查询标签", description = "根据产品ID查询该产品关联的所有标签")
    @GetMapping
    public Result<List<BusTags>> queryByProductId(@RequestParam String productId) {
        LambdaQueryWrapper<ProductTag> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductTag::getProductId, productId);
        List<ProductTag> list = pTagService.list(lqw);

        List<String> tagIds = list.stream().map(ProductTag::getTagId).toList();
        if (tagIds.isEmpty()) {
            return Result.ok(List.of());
        }

        return Result.ok(tagsService.listByIds(tagIds));
    }

    @Operation(summary = "添加标签", description = "添加新的标签")
    @PostMapping
    public Result<?> add(@RequestBody BusTags product) {
        tagsService.save(product);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "编辑标签", description = "根据ID编辑标签信息")
    @PutMapping
    public Result<?> edit(@RequestBody BusTags checkForm) {
        tagsService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @Operation(summary = "删除标签", description = "根据ID删除标签")
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

package cn.cikian.shop.product.controller;


import cn.cikian.shop.product.entity.ProductImg;
import cn.cikian.shop.product.service.ProductImgService;
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
@RequestMapping("/api/proimg")
@Tag(name = "商品图片", description = "商品图片相关接口")
public class ProductImgController {
    @Autowired
    private ProductImgService proImgService;

    @Operation(summary = "查询商品图片分页列表", description = "根据分页参数和查询条件查询商品图片分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<ProductImg>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        LambdaQueryWrapper<ProductImg> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<ProductImg> page = new Page<>(pageNo, pageSize);
        IPage<ProductImg> pageList = proImgService.page(page, lqw);
        return Result.OK(pageList);
    }

    @Operation(summary = "根据ID查询商品图片", description = "根据ID查询商品图片详情")
    @GetMapping("/{id}")
    public Result<ProductImg> query(@PathVariable String id) {
        return Result.ok(proImgService.getById(id));
    }

    @Operation(summary = "根据商品ID查询商品图片列表", description = "根据商品ID查询商品图片列表")
    @GetMapping
    public Result<List<ProductImg>> queryByProductId(@RequestParam String productId) {
        LambdaQueryWrapper<ProductImg> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductImg::getProductId, productId);
        return Result.ok(proImgService.list(lqw));
    }

    @Operation(summary = "添加商品图片", description = "添加新的商品图片")
    @PostMapping
    public Result<?> add(@RequestBody ProductImg product) {
        proImgService.save(product);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "编辑商品图片", description = "根据ID编辑商品图片信息")
    @PutMapping
    public Result<?> edit(@RequestBody ProductImg checkForm) {
        proImgService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @Operation(summary = "删除商品图片", description = "根据ID删除商品图片")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        proImgService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<ProductImg> lqw, Map<String, String[]> map) {
        if (map.containsKey("description")) {
            lqw.like(ProductImg::getDescription, map.get("description")[0]);
        }
    }
}

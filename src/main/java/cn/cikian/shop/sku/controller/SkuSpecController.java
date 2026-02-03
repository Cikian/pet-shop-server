package cn.cikian.shop.sku.controller;


import cn.cikian.shop.sku.entity.SkuSpec;
import cn.cikian.shop.sku.service.SkuSpecsService;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:32
 */

@RestController
@RequestMapping("/api/skuSpec")
public class SkuSpecController {
    @Autowired
    private SkuSpecsService skuSpecService;

    @GetMapping(value = "/list")
    public Result<IPage<SkuSpec>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        LambdaQueryWrapper<SkuSpec> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<SkuSpec> page = new Page<>(pageNo, pageSize);
        IPage<SkuSpec> pageList = skuSpecService.page(page, lqw);
        return Result.OK(pageList);
    }

    @GetMapping("/{id}")
    public Result<SkuSpec> query(@PathVariable String id) {
        return Result.ok(skuSpecService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody SkuSpec product) {
        skuSpecService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody SkuSpec checkForm) {
        skuSpecService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        skuSpecService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<SkuSpec> lqw, Map<String, String[]> map) {
        if (map.containsKey("specKeyId")) {
            lqw.like(SkuSpec::getSpecKeyId, map.get("specKeyId")[0]);
        }
    }
}

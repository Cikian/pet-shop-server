package cn.cikian.shop.controller;


import cn.cikian.shop.entity.BusSku;
import cn.cikian.shop.service.BusSkusService;
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
@RequestMapping("/api/sku")
public class SkuController {
    @Autowired
    private BusSkusService skuService;

    @GetMapping(value = "/list")
    public Result<IPage<BusSku>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        LambdaQueryWrapper<BusSku> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<BusSku> page = new Page<>(pageNo, pageSize);
        IPage<BusSku> pageList = skuService.page(page, lqw);
        return Result.OK(pageList);
    }

    @GetMapping("/{id}")
    public Result<BusSku> query(@PathVariable String id) {
        return Result.ok(skuService.getById(id));
    }

    @GetMapping
    public Result<List<BusSku>> queryByProductId(@RequestParam String productId) {
        LambdaQueryWrapper<BusSku> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusSku::getProductId, productId);
        return Result.ok(skuService.list(lqw));
    }

    @PostMapping
    public Result<?> add(@RequestBody BusSku product) {
        skuService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody BusSku checkForm) {
        skuService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        skuService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<BusSku> lqw, Map<String, String[]> map) {
        if (map.containsKey("skuCode")) {
            lqw.like(BusSku::getSkuCode, map.get("skuCode")[0]);
        }
    }
}

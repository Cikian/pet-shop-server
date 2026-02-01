package cn.cikian.shop.controller;


import cn.cikian.shop.entity.BusCategory;
import cn.cikian.shop.service.BusCategoryService;
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
@RequestMapping("/api/cate")
public class CategoryController {
    @Autowired
    private BusCategoryService cateService;

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

    @PostMapping
    public Result<?> add(@RequestBody BusCategory product) {
        cateService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody BusCategory checkForm) {
        cateService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
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

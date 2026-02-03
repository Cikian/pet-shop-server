package cn.cikian.shop.spec.controller;


import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.service.SpecValuesService;
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
@RequestMapping("/api/spvalue")
public class SpecValuesController {
    @Autowired
    private SpecValuesService specValuesService;

    @GetMapping(value = "/list")
    public Result<IPage<SpecValues>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        LambdaQueryWrapper<SpecValues> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<SpecValues> page = new Page<>(pageNo, pageSize);
        IPage<SpecValues> pageList = specValuesService.page(page, lqw);
        return Result.OK(pageList);
    }

    @GetMapping("/{id}")
    public Result<SpecValues> query(@PathVariable Long id) {
        return Result.ok(specValuesService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody SpecValues product) {
        specValuesService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody SpecValues checkForm) {
        specValuesService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        specValuesService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<SpecValues> lqw, Map<String, String[]> map) {
        if (map.containsKey("value")) {
            lqw.like(SpecValues::getValue, map.get("value")[0]);
        }
    }
}

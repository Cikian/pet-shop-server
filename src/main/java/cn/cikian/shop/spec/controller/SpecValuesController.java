package cn.cikian.shop.spec.controller;


import cn.cikian.shop.spec.entity.SpecValues;
import cn.cikian.shop.spec.service.SpecValuesService;
import cn.cikian.system.sys.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@Tag(name = "规格值", description = "规格值相关接口")
@RequestMapping("/api/spvalue")
public class SpecValuesController {
    @Autowired
    private SpecValuesService specValuesService;

    @Operation(summary = "查询规格值分页列表", description = "根据分页参数和查询条件查询规格值分页列表")
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

    @Operation(summary = "根据ID查询规格值", description = "根据ID查询规格值详情")
    @GetMapping("/{id}")
    public Result<SpecValues> query(@PathVariable Long id) {
        return Result.ok(specValuesService.getById(id));
    }

    @Operation(summary = "添加规格值", description = "添加新的规格值")
    @PostMapping
    public Result<?> add(@RequestBody SpecValues product) {
        specValuesService.save(product);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "编辑规格值", description = "根据ID编辑规格值信息")
    @PutMapping
    public Result<?> edit(@RequestBody SpecValues checkForm) {
        specValuesService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @Operation(summary = "删除规格值", description = "根据ID删除规格值")
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

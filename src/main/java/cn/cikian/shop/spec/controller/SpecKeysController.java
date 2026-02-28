package cn.cikian.shop.spec.controller;


import cn.cikian.shop.spec.entity.SpecKeys;
import cn.cikian.shop.spec.entity.vo.AddSpecVo;
import cn.cikian.shop.spec.service.SpecKeysService;
import cn.cikian.system.sys.entity.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RestController
@Tag(name = "规格键", description = "规格键相关接口")
@RequestMapping("/api/spkey")
public class SpecKeysController {
    @Autowired
    private SpecKeysService specKeysService;

    @Operation(summary = "查询规格键分页列表", description = "根据分页参数和查询条件查询规格键分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<SpecKeys>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        LambdaQueryWrapper<SpecKeys> lqw = new LambdaQueryWrapper<>();

        Map<String, String[]> parameterMap = req.getParameterMap();
        getQueryWrapper(lqw, parameterMap);

        Page<SpecKeys> page = new Page<>(pageNo, pageSize);
        IPage<SpecKeys> pageList = specKeysService.page(page, lqw);
        return Result.OK(pageList);
    }

    @Operation(summary = "根据ID查询规格键", description = "根据规格键ID查询规格键详情")
    @GetMapping("/{id}")
    public Result<SpecKeys> query(@PathVariable String id) {
        return Result.ok(specKeysService.getById(id));
    }

    @Operation(summary = "根据产品ID查询规格键", description = "根据产品ID查询该产品的所有规格键")
    @GetMapping("/product")
    public Result<List<AddSpecVo>> queryByProductId(@RequestParam String productId) {
        List<AddSpecVo> byProductId = specKeysService.findByProductId(productId);
        return Result.ok(byProductId);
    }

    @Operation(summary = "添加规格键", description = "添加新的规格键")
    @PostMapping
    public Result<?> add(@RequestBody SpecKeys product) {
        specKeysService.save(product);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "编辑规格键", description = "根据ID编辑规格键信息")
    @PutMapping
    public Result<?> edit(@RequestBody SpecKeys checkForm) {
        specKeysService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

    @Operation(summary = "删除规格键", description = "根据ID删除规格键")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        specKeysService.removeById(id);
        return Result.ok("删除成功！");
    }

    private void getQueryWrapper(LambdaQueryWrapper<SpecKeys> lqw, Map<String, String[]> map) {
        if (map.containsKey("name")) {
            lqw.like(SpecKeys::getName, map.get("name")[0]);
        }
    }
}

package cn.cikian.shop.controller;


import cn.cikian.shop.entity.SpecKeys;
import cn.cikian.shop.entity.vo.AddSpecVo;
import cn.cikian.shop.service.SpecKeysService;
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
@RequestMapping("/api/spkey")
public class SpecKeysController {
    @Autowired
    private SpecKeysService specKeysService;

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

    @GetMapping("/{id}")
    public Result<SpecKeys> query(@PathVariable String id) {
        return Result.ok(specKeysService.getById(id));
    }

    @GetMapping("/product")
    public Result<List<AddSpecVo>> queryByProductId(@RequestParam String productId) {
        List<AddSpecVo> byProductId = specKeysService.findByProductId(productId);
        return Result.ok(byProductId);
    }

    @PostMapping
    public Result<?> add(@RequestBody SpecKeys product) {
        specKeysService.save(product);
        return Result.ok("添加成功！");
    }

    @PutMapping
    public Result<?> edit(@RequestBody SpecKeys checkForm) {
        specKeysService.updateById(checkForm);
        return Result.ok(checkForm, "编辑成功！");
    }

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

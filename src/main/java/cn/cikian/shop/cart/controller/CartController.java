package cn.cikian.shop.cart.controller;


import cn.cikian.shop.cart.entity.BusCart;
import cn.cikian.shop.cart.entity.CartVo;
import cn.cikian.shop.cart.service.BusCartService;
import cn.cikian.shop.cart.service.CartVoService;
import cn.cikian.shop.category.entity.BusTags;
import cn.cikian.shop.category.entity.ProductTag;
import cn.cikian.shop.category.service.ProductTagService;
import cn.cikian.system.core.exception.CikException;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.entity.enmu.SysStatus;
import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.utils.AuthUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @since 2026-03-03 15:53
 */

@Slf4j
@RestController
@RequestMapping("/api/cart")
@Tag(name = "购物车", description = "购物车相关接口")
public class CartController {
    @Autowired
    private BusCartService cartService;
    @Autowired
    private CartVoService cartVoService;


    @Operation(summary = "购物车分页列表", description = "根据分页参数和查询条件查询购物车分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<CartVo>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        LoginUser loginUser = AuthUtils.getLoginUser();
        if (loginUser == null) {
            throw new CikException(SysStatus.NEED_LOGIN);
        }

        LambdaQueryWrapper<CartVo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CartVo::getUserId, loginUser.getUser().getId());
        lqw.orderByDesc(CartVo::getCreateTime);

        Page<CartVo> page = new Page<>(pageNo, pageSize);
        IPage<CartVo> pageList = cartVoService.page(page, lqw);
        return Result.OK(pageList);
    }

    @Operation(summary = "添加sku到购物车", description = "添加sku到购物车")
    @PostMapping
    public Result<?> add(@RequestBody BusCart cart) {
        LoginUser loginUser = AuthUtils.getLoginUser();
        if (loginUser == null) {
            throw new CikException(SysStatus.NEED_LOGIN);
        }

        String userId = loginUser.getUser().getId();

        String skuId = cart.getSkuId();
        if (skuId == null) {
            throw new CikException("skuId不能为空");
        }

        BusCart existCart = cartService.getCartByUserAndSku(userId, skuId);
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            cartService.updateById(existCart);
        } else {
            cart.setUserId(userId);
            cartService.save(cart);
        }
        return Result.ok("添加成功！");
    }
}

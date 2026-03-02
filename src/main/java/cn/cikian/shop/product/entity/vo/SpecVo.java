package cn.cikian.shop.product.entity.vo;


import lombok.Data;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-03-02 10:45
 */

@Data
public class SpecVo {
    private String name;
    private List<String> values;
}

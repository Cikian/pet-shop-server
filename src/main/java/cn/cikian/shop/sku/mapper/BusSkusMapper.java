package cn.cikian.shop.sku.mapper;

import cn.cikian.shop.sku.entity.BusSku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Mapper
public interface BusSkusMapper extends BaseMapper<BusSku> {

    @Select("select * from bus_sku where product_id = #{productId} and del_flag = 0")
    List<BusSku> queryByProductId(String productId);
}





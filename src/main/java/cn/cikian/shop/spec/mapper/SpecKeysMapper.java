package cn.cikian.shop.spec.mapper;

import cn.cikian.shop.spec.entity.SpecKeys;
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
public interface SpecKeysMapper extends BaseMapper<SpecKeys> {
    @Select("select * from bus_spec_keys where product_id = #{productId} and del_flag = 0")
    List<SpecKeys> queryByProductId(String productId);
}





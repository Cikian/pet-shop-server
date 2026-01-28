package cn.cikian.shop.sys.mapper;


import cn.cikian.shop.sys.entity.SysRole;
import cn.cikian.shop.sys.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:18
 */

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<String> selectPermsByUserId(String username);
}

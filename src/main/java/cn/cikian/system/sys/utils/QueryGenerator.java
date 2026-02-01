package cn.cikian.system.sys.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-31 02:39
 */

@Slf4j
public class QueryGenerator {

    /**
     * 获取查询条件构造器QueryWrapper实例 通用查询条件已被封装完成
     * @param searchObj 查询实体
     * @param parameterMap request.getParameterMap()
     * @return QueryWrapper实例
     */
    public static <T> QueryWrapper<T> initQueryWrapper(T searchObj, Map<String, String[]> parameterMap){
        long start = System.currentTimeMillis();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
//        installMplus(queryWrapper, searchObj, parameterMap);
        log.debug("---查询条件构造器初始化完成,耗时:"+(System.currentTimeMillis()-start)+"毫秒----");
        return queryWrapper;
    }
}

package cn.cikian.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Data
@TableName("bus_category")
public class BusCategory implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private String cateCode;
    /**
     *
     */
    private Integer delFlag;
}

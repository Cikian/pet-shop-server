package cn.cikian.shop.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-03 17:33
 */

@Data
@TableName("bus_tags")
public class BusTags implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 标签分类
     */
    private String tagCategory;
    @TableLogic
    private Boolean delFlag;
}

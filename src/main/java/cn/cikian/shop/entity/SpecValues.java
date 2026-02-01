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
@TableName("bus_spec_values")
public class SpecValues implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 规格键ID
     */
    private String specKeyId;
    /**
     * 规格值，如：红色、16G、500ml
     */
    private String value;
    /**
     * 规格图片（如颜色色卡）
     */
    private String image;
    /**
     * 排序
     */
    private Integer sortOrder;
    private Boolean delFlag;
}

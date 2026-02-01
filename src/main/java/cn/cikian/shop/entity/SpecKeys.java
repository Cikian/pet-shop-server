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
@TableName("bus_spec_keys")
public class SpecKeys implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 规格名称，如：颜色、尺寸、容量
     */
    private String name;
    /**
     * 关联的分类ID（可为空，表示通用规格）
     */
    private String categoryId;
    /**
     * 输入类型
     */
    private String inputType;
    /**
     * 排序
     */
    private Integer sortOrder;
    private Boolean delFlag;
}

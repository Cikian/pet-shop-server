package cn.cikian.shop.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "商品标签实体")
public class BusTags implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "标签 ID")
    private String id;
    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String tagName;
    /**
     * 标签分类
     */
    @Schema(description = "标签分类")
    private String tagCategory;
    @TableLogic
    @Schema(description = "删除标记")
    private Boolean delFlag;
}

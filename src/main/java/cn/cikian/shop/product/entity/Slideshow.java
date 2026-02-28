package cn.cikian.shop.product.entity;

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
 * @since 2026-02-09 15:06
 */

@Data
@TableName("bus_slideshow")
@Schema(name = "首页轮播图配置实体")
public class Slideshow implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @Schema(description = "商品id")
    private String productId;
    @Schema(description = "1-启用 0-禁用")
    private String status;
    @Schema(description = "展示图")
    private String displayImg;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "按钮文字")
    private String btnText;
    @Schema(description = "排序")
    private Integer sortOrder;
    @TableLogic
    @Schema(description = "删除标记")
    private String delFlag;
}

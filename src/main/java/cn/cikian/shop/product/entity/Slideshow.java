package cn.cikian.shop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
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
public class Slideshow implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty("商品id")
    private String productId;
    @ApiModelProperty("1-启用 0-禁用")
    private String status;
    @ApiModelProperty("展示图")
    private String displayImg;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("按钮文字")
    private String btnText;
    @ApiModelProperty("排序")
    private Integer sortOrder;
    @TableLogic
    private String delFlag;
}

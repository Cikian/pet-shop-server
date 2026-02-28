package cn.cikian.system.sys.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:30
 */

@Data
@Schema(name = "登录请求DTO")
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
    @Schema(description = "验证码")
    private String captcha;
    @Schema(description = "验证码key")
    private String captchaKey;
}

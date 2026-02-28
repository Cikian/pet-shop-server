package cn.cikian.system.sys.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:32
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "登录响应")
public class LoginResponse {
    @Schema(description = "token")
    private String accessToken;
    @Schema(description = "刷新token")
    private String refreshToken;
    @Schema(description = "token类型")
    private String tokenType;
    @Schema(description = "过期时间，单位秒")
    private Long expiresIn;
    @Schema(description = "用户信息")
    private UserVO user;
    @Schema(description = "权限列表")
    private List<String> authorities;
}

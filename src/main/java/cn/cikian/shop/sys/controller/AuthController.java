package cn.cikian.shop.sys.controller;

import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.entity.dto.LoginRequest;
import cn.cikian.shop.sys.entity.dto.RegisterRequest;
import cn.cikian.shop.sys.entity.vo.LoginResponse;
import cn.cikian.shop.sys.entity.vo.Result;
import cn.cikian.shop.sys.entity.vo.UserVO;
import cn.cikian.shop.sys.service.SysUserService;
import cn.cikian.shop.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 16:28
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private SysUserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        log.info("用户登录: {}", loginRequest.getUsername());

        // 验证码验证（如果需要）
        // validateCaptcha(loginRequest.getCaptchaKey(), loginRequest.getCaptcha());

        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户详情
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 生成token
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        // 获取用户信息
        SysUser user = userService.getByUsername(loginRequest.getUsername());
        UserVO userVO = convertToVO(user);

        // 获取权限
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 更新最后登录时间和IP
        String clientIp = getClientIp(request);
        userService.updateLastLogin(user.getId(), clientIp);

        // 构建响应
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime())
                .user(userVO)
                .authorities(authorities)
                .build();

        log.info("用户登录成功: {}", loginRequest.getUsername());

        return Result.ok(loginResponse);
    }

    @PostMapping("/register")
    public Result<UserVO> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        log.info("用户注册: {}", registerRequest.getUsername());

        // 验证码验证
        // validateCaptcha(registerRequest.getCaptchaKey(), registerRequest.getCaptcha());

        SysUser user = userService.register(registerRequest);
        UserVO userVO = convertToVO(user);

        log.info("用户注册成功: {}", registerRequest.getUsername());

        return Result.ok(userVO, "注册成功");
    }

    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(400, "无效的token");
        }

        String refreshToken = authHeader.substring(7);

        // 验证token类型
        String tokenType = jwtTokenUtil.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            return Result.error(400, "无效的refresh token");
        }

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            return Result.error(400, "refresh token已过期");
        }

        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        tokens.put("tokenType", "Bearer");
        tokens.put("expiresIn", String.valueOf(
                jwtTokenUtil.getExpirationDateFromToken(newAccessToken).getTime()
        ));

        return Result.ok(tokens, "token刷新成功");
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = userService.getByUsername(username);
        UserVO userVO = convertToVO(user);

        return Result.ok(userVO, "获取成功");
    }

    @GetMapping("/validate")
    public Result<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(400, "无效的token");
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtTokenUtil.validateToken(token);

        Map<String, Object> result = new HashMap<>();
        result.put("valid", isValid);

        if (isValid) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
            Long remainingTime = jwtTokenUtil.getRemainingTime(token);

            result.put("username", username);
            result.put("expiration", expiration);
            result.put("remainingTime", remainingTime);
        }

        return Result.ok(result, isValid ? "token有效" : "token无效");
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("用户登出: {}", authentication.getName());
        }

        SecurityContextHolder.clearContext();

        // 清除cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return Result.OK("登出成功");
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(
            @RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.ok(exists, "查询成功");
    }

    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(
            @RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.ok(exists, "查询成功");
    }

    private UserVO convertToVO(SysUser user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

package cn.cikian.shop.sys.controller;

import cn.cikian.shop.core.utils.RedisCache;
import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.entity.dto.LoginRequest;
import cn.cikian.shop.sys.entity.dto.LoginUser;
import cn.cikian.shop.sys.entity.dto.RegisterRequest;
import cn.cikian.shop.sys.entity.vo.LoginResponse;
import cn.cikian.shop.sys.entity.vo.Result;
import cn.cikian.shop.sys.entity.vo.UserVO;
import cn.cikian.shop.sys.service.SysUserService;
import cn.cikian.shop.sys.utils.JwtUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysUserService userService;

    @RequestMapping("/hello")
    @PreAuthorize("hasAuthority('test')")
    public String hello(){
        return "hello";
    }


    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        log.info("用户登录: {}", loginRequest.getUsername());

        // 验证码验证（如果需要）
        // validateCaptcha(loginRequest.getCaptchaKey(), loginRequest.getCaptcha());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        // 认证用户
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误！");
        }

        // SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户详情
        LoginUser userDetails = (LoginUser) authenticate.getPrincipal();

        // 使用userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        // authenticate存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);

        // 生成token
        String accessToken = JwtUtil.createJWT(JSONObject.toJSONString(userDetails));

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
                .tokenType("Bearer")
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

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = userService.getByUsername(username);
        UserVO userVO = convertToVO(user);

        return Result.ok(userVO, "获取成功");
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("用户登出: {}", authentication.getName());
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        SecurityContextHolder.clearContext();

        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("login:" + userid);

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

package cn.cikian.system.sys.controller;

import cn.cikian.system.core.utils.RedisCache;
import cn.cikian.system.sys.entity.SysUser;
import cn.cikian.system.sys.entity.dto.LoginRequest;
import cn.cikian.system.sys.entity.dto.LoginUser;
import cn.cikian.system.sys.entity.dto.RegisterRequest;
import cn.cikian.system.sys.entity.vo.LoginResponse;
import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.entity.vo.UserVO;
import cn.cikian.system.sys.service.SysUserService;
import cn.cikian.system.sys.utils.JwtUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping("/hello")
    @PreAuthorize("hasAuthority('test')")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/hello2")
    @PreAuthorize("hasAuthority('test:hello')")
    public String hello2() {
        return "hello2";
    }

    @RequestMapping("/hello3")
    @PreAuthorize("hasRole('TESTER')")
    public String hello3() {
        return "hello3";
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
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // authenticate存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);
        // 生成token
        String accessToken = JwtUtil.createJWT(JSONObject.toJSONString(loginUser));
        // 获取权限
        List<String> authorities = loginUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 构建响应
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .user(new UserVO(loginUser))
                .authorities(authorities)
                .build();

        // 更新最后登录时间和IP
        String clientIp = getClientIp(request);
        userService.updateLastLogin(userId, clientIp);
        log.info("用户登录成功: {}，登录IP: {}", loginRequest.getUsername(), clientIp);

        return Result.ok(loginResponse);
    }

    @PostMapping("/register")
    public Result<UserVO> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册: {}", registerRequest.getUsername());

        // 验证码验证
        // validateCaptcha(registerRequest.getCaptchaKey(), registerRequest.getCaptcha());

        SysUser user = userService.register(registerRequest);
        UserVO userVO = new UserVO(user);

        log.info("用户注册成功: {}", registerRequest.getUsername());

        return Result.ok(userVO, "注册成功");
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = userService.getByUsername(username);
        UserVO userVO = new UserVO(user);

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

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * 跳转到Google登录页面
     * @return
     */
    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }

    /**
     * 处理Google登录回调
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/google/callback")
    public Result<LoginResponse> googleCallback(HttpServletRequest request, HttpServletResponse response,
                                                @RequestParam String state,
                                                @RequestParam String scope,
                                                @RequestParam Integer authuser,
                                                @RequestParam String prompt) {
        log.info("Google登录回调: state={}, scope={}, authuser={}, prompt={}", state, scope, authuser, prompt);

        // 从SecurityContext中获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("Google登录回调: authentication为null");
            return Result.error("认证失败，请重新登录");
        }

        log.info("Google登录回调: {}", authentication.getName());
        log.info("Authentication类型: {}", authentication.getClass().getName());
        log.info("Principal类型: {}", authentication.getPrincipal().getClass().getName());

        // 检查principal类型
        if (!(authentication.getPrincipal() instanceof DefaultOAuth2User)) {
            log.error("Google登录回调: principal不是DefaultOAuth2User类型，而是{}", authentication.getPrincipal().getClass().getName());
            return Result.error("认证失败，用户信息获取错误");
        }

        // 获取Google用户信息
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        String sub = oauth2User.getAttribute("sub"); // Google用户唯一标识

        log.info("Google用户信息: email={}, name={}, picture={}", email, name, picture);

        // 创建或更新系统用户
        SysUser user = userService.createOrUpdateGoogleUser(email, email, name, picture);

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 构建LoginUser
        LoginUser loginUser = new LoginUser(user, authorities);

        // 存入Redis
        Long userId = user.getId();
        redisCache.setCacheObject("login:" + userId, loginUser);

        // 生成token
        String accessToken = JwtUtil.createJWT(JSONObject.toJSONString(loginUser));

        // 获取权限
        List<String> authorityList = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 构建响应
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .user(new UserVO(loginUser))
                .authorities(authorityList)
                .build();

        // 更新最后登录时间和IP
        String clientIp = getClientIp(request);
        userService.updateLastLogin(userId, clientIp);
        log.info("Google登录成功: {}，登录IP: {}", email, clientIp);

        return Result.ok(loginResponse);
    }
}

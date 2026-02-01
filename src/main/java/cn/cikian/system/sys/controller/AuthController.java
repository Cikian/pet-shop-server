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
import com.alibaba.fastjson2.JSON;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

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
        String userId = loginUser.getUser().getId();
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

        String userid = loginUser.getUser().getId();
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
     *
     * @return
     */
    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }

    /**
     * 处理Google登录回调
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/google/callback")
    public Result<LoginResponse> googleCallback(HttpServletRequest request, HttpServletResponse response,
                                                @RequestParam String code,
                                                @RequestParam String state,
                                                @RequestParam String scope,
                                                @RequestParam Integer authuser,
                                                @RequestParam String prompt) {
        log.info("收到Google回调: code={}, state={}, scope={}, authuser={}, prompt={}", code, state, scope, authuser, prompt);

        GoogleTokenResponse tokenResponse = exchangeCodeForToken(code);
        if (tokenResponse == null) {
            return Result.error("获取token失败");
        }
        // 2. 从响应中获取令牌
        String accessToken = tokenResponse.getAccessToken();
        String idTokenString = tokenResponse.getIdToken();

        // 3. 验证并解析ID令牌获取用户信息
        GoogleIdToken idToken = null;
        try {
            idToken = tokenResponse.parseIdToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GoogleIdToken.Payload payload = idToken.getPayload();

        // 4. 提取用户信息
        Map<String, Object> userInfo = extractUserInfoFromPayload(payload);
        Map<String, Object> success = Map.of(
                "success", true,
                "access_token", accessToken,
                "id_token", idTokenString,
                "expires_in", tokenResponse.getExpiresInSeconds(),
                "token_type", tokenResponse.getTokenType(),
                "scope", tokenResponse.getScope(),
                "user", userInfo
        );
        System.out.println(success);
        return Result.OK();
    }

    private Map<String, Object> extractUserInfoFromPayload(GoogleIdToken.Payload payload) {
        Map<String, Object> userInfo = new HashMap<>();

        // 从ID Token中提取标准声明
        userInfo.put("sub", payload.getSubject()); // 用户唯一标识
        userInfo.put("email", payload.getEmail());
        userInfo.put("email_verified", payload.getEmailVerified());
        userInfo.put("name", payload.get("name"));
        userInfo.put("given_name", payload.get("given_name"));
        userInfo.put("family_name", payload.get("family_name"));
        userInfo.put("picture", payload.get("picture"));
        userInfo.put("locale", payload.get("locale"));

        // 添加时间戳
        userInfo.put("issued_at", payload.getIssuedAtTimeSeconds());
        userInfo.put("expires_at", payload.getExpirationTimeSeconds());

        return userInfo;
    }

    private GoogleTokenResponse exchangeCodeForToken(String code) {
        try {
            return new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    clientId,
                    clientSecret,
                    code,
                    "http://localhost:18500/api/v1/auth/google/callback"
            ).execute();
        } catch (IOException e) {
            log.error("Failed to exchange code for token", e);
        }
        return null;
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }

    private boolean validateState(String state) {
        // 实现state验证逻辑，通常与session中存储的state比较
        // 这里简化处理，实际生产环境需要严格验证
        return state != null && !state.isEmpty();
    }

    private LoginUser processUserInfo(Map<String, Object> userInfo) {
        // 处理用户信息，保存到数据库或更新现有用户
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");
        String sub = (String) userInfo.get("sub"); // Google用户唯一ID

        // 这里实现你的用户处理逻辑
        SysUser sysUser = new SysUser();
        sysUser.setEmail(email);
        sysUser.setNickname(name);
        sysUser.setAvatar(picture);

        return new LoginUser(sysUser);
    }

}

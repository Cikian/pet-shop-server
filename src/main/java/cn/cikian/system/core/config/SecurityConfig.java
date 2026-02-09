package cn.cikian.system.core.config;


import cn.cikian.system.core.security.AccessDeniedHandlerImpl;
import cn.cikian.system.core.security.JwtAuthenticationEntryPoint;
import cn.cikian.system.core.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 15:51
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;
    @Autowired
    private LogoutHandler logoutHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/public/**",
                                "/api/v1/captcha/**",
                                "/api/v1/file/download/**",
                                "/api/v1/products/public/**",
                                "/api/v1/categories/public/**",
                                "/api/v1/email/**",
                                "/api/v1/test/**",
                                "/api/test/**",
                                "/oauth2/**",
                                "/api/home/**"
                        ).permitAll()

                        // 需要认证但不需要特定权限的接口
                        .requestMatchers(
                                "/api/goods/**",
                                "/api/cate/**",
                                "/api/proimg/**",
                                "/api/sku/**",
                                "/api/skuSpec/**",
                                "/api/spvalue/**",
                                "/api/spkey/**",
                                "/api/tags/**",
                                "/api/user/**",
                                "/api/role/**"
                        ).authenticated()

                        // druid相关
                        .requestMatchers(
                                "/druid/**"
                        ).permitAll()

                        // Swagger 相关
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/v2/api-docs"
                        ).permitAll()

                        // 静态资源
                        .requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/error",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // 需要用户权限的接口
                        .requestMatchers(
                                "/api/v1/user/**",
                                "/api/v1/cart/**",
                                "/api/v1/order/**",
                                "/api/v1/address/**",
                                "/api/v1/profile/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 需要管理员权限的接口
                        .requestMatchers(
                                "/api/v1/admin/**",
                                "/api/v1/system/**",
                                "/api/v1/management/**"
                        ).hasRole("ADMIN")

                        // 需要特定权限的接口
                        .requestMatchers(
                                "/api/v1/products/create",
                                "/api/v1/products/update/**",
                                "/api/v1/products/delete/**"
                        ).hasAuthority("PRODUCT_WRITE")

                        .requestMatchers(
                                "/api/v1/orders/export"
                        ).hasAuthority("ORDER_EXPORT")

                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )

                // 配置会话管理为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置认证提供者
                .authenticationProvider(authenticationProvider(userDetailsService))

                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 配置登出
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            response.setStatus(200);
                        })
                )

                // 配置OAuth2登录
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/v1/auth/google/login")
                        .defaultSuccessUrl("/api/v1/auth/google/callback", true)
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 在 Spring Security 6.x 中，推荐通过构造器或 setter 设置
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        // 设置为false，不隐藏用户不存在异常，这样可以在JwtAuthenticationEntryPoint中区分不同的异常
        authProvider.setHideUserNotFoundExceptions(false);

        // Spring Security 6.x 默认不隐藏用户不存在异常
        // 如果还需要配置，可以这样：
        authProvider.setUserDetailsPasswordService(null);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "http://127.0.0.1:5173"
        ));

        // 允许的方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "X-CSRF-Token",
                "Cache-Control",
                "Pragma",
                "X-Total-Count"
        ));

        // 暴露的响应头
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "X-Total-Count",
                "X-Filename"
        ));

        // 是否允许发送 Cookie
        configuration.setAllowCredentials(true);

        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

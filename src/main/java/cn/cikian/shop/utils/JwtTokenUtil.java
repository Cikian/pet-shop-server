package cn.cikian.shop.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 15:19
 */

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret:your-256-bit-secret-change-this-in-production-environment-2024}")
    private String secret;

    @Value("${jwt.access-token.expiration:3600}") // 1小时
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:604800}") // 7天
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer:mall-system}")
    private String issuer;

    // 生成密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查token是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问token
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // 添加用户权限
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("authorities", authorities);
        claims.put("type", "ACCESS");

        return doGenerateToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    /**
     * 生成刷新token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return doGenerateToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }

    /**
     * 生成token
     */
    private String doGenerateToken(Map<String, Object> claims, String subject, Long expiration) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(expiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .setId(UUID.randomUUID().toString()) // JWT ID
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 验证token是否有效
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("JWT token已过期: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("不支持的JWT token: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("无效的JWT token: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("JWT token签名无效: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT token验证失败: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 获取token类型
     */
    public String getTokenType(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    /**
     * 获取token剩余有效时间（秒）
     */
    public Long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(remaining / 1000, 0);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 从token中获取权限列表
     */
    public List<String> getAuthoritiesFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Object authoritiesObj = claims.get("authorities");
            if (authoritiesObj instanceof List) {
                return (List<String>) authoritiesObj;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("获取权限列表失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

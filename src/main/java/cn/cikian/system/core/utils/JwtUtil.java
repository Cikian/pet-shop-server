package cn.cikian.system.core.utils;

import cn.cikian.system.sys.entity.dto.LoginUser;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author Cikian
 * @version 1.3
 * @implNote 补全了生成 Token (createJWT) 和还原对象 (getSubObject) 方法，统一使用 fastjson2
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-01-28 15:19
 */
@Slf4j
@Component
public class JwtUtil {

    // 有效期为一个小时
    public static final Long JWT_TTL = 60 * 60 * 1000L;

    // 设置秘钥明文
    public static final String JWT_KEY = "K7gNU3sdo+OL0wNhqoVWhr3g6s1xYv72ol/pe/Unols=";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成加密后的秘钥 secretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return Keys.hmacShaKeyFor(encodedKey);
    }

    /**
     * 新增：生成最基础的 JWT (默认一小时过期)
     *
     * @param subject 存入的载荷内容 (通常是 LoginUser 对象的 JSON 字符串)
     * @return jwt token 字符串
     */
    public static String createJWT(String subject) {
        return createJWT(subject, JWT_TTL, getUUID());
    }

    /**
     * 新增：生成可控过期时间的 JWT
     *
     * @param subject   存入的载荷内容
     * @param ttlMillis 有效期时间 (毫秒)
     * @param uuid      jwt 的唯一标识 ID
     * @return jwt token 字符串
     */
    public static String createJWT(String subject, Long ttlMillis, String uuid) {
        JwtBuilder builder = Jwts.builder()
                .setId(uuid)                                // 设置JWT的唯一标识
                .setSubject(subject)                        // 主题 (通常存序列化后的用户信息)
                .setIssuer("cikian")                        // 签发者
                .setIssuedAt(new Date())                    // 签发时间
                .signWith(generalKey(), SignatureAlgorithm.HS256); // 签名算法及密钥

        if (ttlMillis >= 0) {
            long expMillis = System.currentTimeMillis() + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);                     // 设置过期时间
        }
        return builder.compact();
    }

    /**
     * 从 JWT 令牌中提取 Subject 并反序列化为指定的 LoginUser 对象
     *
     * @param jwt token 字符串
     * @return 还原后的 LoginUser 实例，解析失败则返回 null
     */
    public static LoginUser getSubObject(String jwt) {
        try {
            Claims claims = parseJWT(jwt);
            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                return null;
            }
            // 统一采用项目中的 fastjson2 将 JSON 字符串反序列化回 LoginUser 实体类
            return JSON.parseObject(subject, LoginUser.class);
        } catch (Exception e) {
            log.error("从 JWT 中提取 LoginUser 对象失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析 JWT
     *
     * @param jwt token 字符串
     * @return Claims 载荷
     * @throws ExpiredJwtException 当 token 过期时抛出
     * @throws SignatureException 当签名无效/被篡改时抛出
     * @throws MalformedJwtException 当 token 格式畸形时抛出
     * @throws JwtException 其他 jjwt 内部异常
     */
    public static Claims parseJWT(String jwt) throws JwtException {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 校验 token
     */
    public static Boolean validateJWT(String jwt) {
        try {
            parseJWT(jwt);
            return true;
        } catch (Exception e) {
            log.warn("JWT 校验未通过: {}", e.getMessage());
            return false;
        }
    }
}
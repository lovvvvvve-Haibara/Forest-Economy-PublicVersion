package edu.scau.forestproject.utils;

import edu.scau.forestproject.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类，用于生成和解析 JWT 令牌。
 */
@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtils(AuthProperties authProperties) {
        String secretBase64 = authProperties.getJwtSecretBase64();
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException("缺少配置项 forest.auth.jwt-secret-base64，请通过环境变量 JWT_SECRET_BASE64 提供密钥。");
        }

        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = authProperties.getJwtExpirationMs();
    }

    public String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMs);
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

package edu.scau.forestproject.security;

import edu.scau.forestproject.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenService {
    private final AuthProperties authProperties;

    public JwtTokenService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + authProperties.getJwtExpirationMs());
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(authProperties.getJwtSecretBase64());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

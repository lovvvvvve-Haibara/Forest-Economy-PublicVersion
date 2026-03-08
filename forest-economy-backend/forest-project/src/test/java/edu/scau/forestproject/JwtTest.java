package edu.scau.forestproject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    private static final String TEST_SECRET_BASE64 =
            "ZGVtb19qd3Rfc2VjcmV0X2Zvcl9vcGVuX3NvdXJjZV9vbmx5X2NoYW5nZV9tZQ==";

    private SecretKey buildTestKey() {
        byte[] keyBytes = Base64.getDecoder().decode(TEST_SECRET_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    public void testGenerateAndParseJwt() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", 1);
        dataMap.put("name", "test");

        SecretKey key = buildTestKey();

        String jwt = Jwts.builder()
                .claims(dataMap)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        Assertions.assertEquals(1, claims.get("id"));
        Assertions.assertEquals("test", claims.get("name"));
    }
}

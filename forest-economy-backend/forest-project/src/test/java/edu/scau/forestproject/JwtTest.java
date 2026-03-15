package edu.scau.forestproject;

import edu.scau.forestproject.config.AuthProperties;
import edu.scau.forestproject.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class JwtTest {

    /** Base64 of "test-secret-for-junit-only", not used in production. */
    private static final String TEST_SECRET_BASE64 = "dGVzdC1zZWNyZXQtZm9yLWp1bml0LW9ubHk=";

    @Test
    void shouldGenerateAndParseToken() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtSecretBase64(TEST_SECRET_BASE64);
        authProperties.setJwtExpirationMs(3600_000);
        JwtTokenService jwtTokenService = new JwtTokenService(authProperties);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("name", "test");

        String token = jwtTokenService.generateToken(claims);
        Claims parsed = jwtTokenService.parseToken(token);

        Assertions.assertEquals(1, parsed.get("id"));
        Assertions.assertEquals("test", parsed.get("name"));
    }
}

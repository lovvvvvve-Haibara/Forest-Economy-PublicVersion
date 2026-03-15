package edu.scau.forestproject.security;

import edu.scau.forestproject.config.ConcurrencyProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IpRateLimiterTest {

    @Test
    void shouldRejectWhenExceedLimitInWindow() {
        ConcurrencyProperties properties = new ConcurrencyProperties();
        properties.getRateLimit().setWindowSeconds(60);
        properties.getRateLimit().setCacheMaxSize(100);

        IpRateLimiter limiter = new IpRateLimiter(properties);
        String key = "login:127.0.0.1";

        Assertions.assertTrue(limiter.allow(key, 2));
        Assertions.assertTrue(limiter.allow(key, 2));
        Assertions.assertFalse(limiter.allow(key, 2));
    }
}

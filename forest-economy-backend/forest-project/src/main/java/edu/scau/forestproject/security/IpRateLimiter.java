package edu.scau.forestproject.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.scau.forestproject.config.ConcurrencyProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IpRateLimiter {
    private final Cache<String, WindowCounter> counterCache;
    private final int windowSeconds;

    public IpRateLimiter(ConcurrencyProperties properties) {
        this.windowSeconds = properties.getRateLimit().getWindowSeconds();
        this.counterCache = Caffeine.newBuilder()
                .maximumSize(properties.getRateLimit().getCacheMaxSize())
                .expireAfterAccess(Math.max(windowSeconds * 2L, 120), TimeUnit.SECONDS)
                .build();
    }

    public boolean allow(String key, int limit) {
        WindowCounter counter = counterCache.get(key, k -> new WindowCounter());
        return counter.tryAcquire(limit, windowSeconds * 1000L);
    }

    static class WindowCounter {
        private long windowStartMs = 0L;
        private final AtomicInteger count = new AtomicInteger(0);

        synchronized boolean tryAcquire(int limit, long windowMs) {
            long now = System.currentTimeMillis();
            if (windowStartMs == 0L || now - windowStartMs >= windowMs) {
                windowStartMs = now;
                count.set(0);
            }
            return count.incrementAndGet() <= limit;
        }
    }
}

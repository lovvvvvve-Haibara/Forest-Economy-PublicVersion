package edu.scau.forestproject.security;

import edu.scau.forestproject.config.ConcurrencyProperties;
import edu.scau.forestproject.exception.BusinessException;
import edu.scau.forestproject.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final ConcurrencyProperties concurrencyProperties;
    private final IpRateLimiter ipRateLimiter;

    public RateLimitInterceptor(ConcurrencyProperties concurrencyProperties, IpRateLimiter ipRateLimiter) {
        this.concurrencyProperties = concurrencyProperties;
        this.ipRateLimiter = ipRateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ConcurrencyProperties.RateLimit conf = concurrencyProperties.getRateLimit();
        if (!conf.isEnabled()) {
            return true;
        }

        String path = request.getRequestURI();
        int limit = resolveLimit(path, conf);
        if (limit <= 0) {
            return true;
        }

        String key = path + ":" + resolveClientIp(request);
        if (!ipRateLimiter.allow(key, limit)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "rate limit exceeded");
        }
        return true;
    }

    private int resolveLimit(String path, ConcurrencyProperties.RateLimit conf) {
        if (path.contains("/api/v1/auth/login") || path.contains("/login")) {
            return conf.getLoginLimitPerIp();
        }
        if (path.contains("/api/v1/auth/register") || path.contains("/register")) {
            return conf.getRegisterLimitPerIp();
        }
        return conf.getProtectedApiLimitPerIp();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}

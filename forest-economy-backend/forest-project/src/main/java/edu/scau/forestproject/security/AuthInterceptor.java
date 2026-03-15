package edu.scau.forestproject.security;

import edu.scau.forestproject.config.AuthProperties;
import edu.scau.forestproject.exception.BusinessException;
import edu.scau.forestproject.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthProperties authProperties;
    private final JwtTokenService jwtTokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthInterceptor(AuthProperties authProperties, JwtTokenService jwtTokenService) {
        this.authProperties = authProperties;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (isWhitelisted(path)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            token = request.getHeader("token");
        }
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "missing token");
        }
        if (token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
        }
        try {
            jwtTokenService.parseToken(token);
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "invalid token");
        }
    }

    private boolean isWhitelisted(String path) {
        return authProperties.getWhiteList().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}

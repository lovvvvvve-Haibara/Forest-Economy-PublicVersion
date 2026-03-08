package edu.scau.forestproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 认证配置。
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "forest.auth")
public class AuthProperties {

    /**
     * Base64 编码的 JWT 密钥。
     */
    private String jwtSecretBase64;

    /**
     * JWT 过期时间，单位毫秒。
     */
    private long jwtExpirationMs = 2592000000L;
}

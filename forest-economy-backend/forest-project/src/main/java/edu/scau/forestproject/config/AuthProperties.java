package edu.scau.forestproject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "forest.auth")
public class AuthProperties {
    private String jwtSecretBase64;
    private long jwtExpirationMs;
    private List<String> whiteList = new ArrayList<>();

    public String getJwtSecretBase64() {
        return jwtSecretBase64;
    }

    public void setJwtSecretBase64(String jwtSecretBase64) {
        this.jwtSecretBase64 = jwtSecretBase64;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }
}

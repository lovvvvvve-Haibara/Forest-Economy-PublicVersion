package edu.scau.forestproject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "forest.concurrency")
public class ConcurrencyProperties {
    private final RateLimit rateLimit = new RateLimit();

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public static class RateLimit {
        private boolean enabled = true;
        private int windowSeconds = 60;
        private int loginLimitPerIp = 30;
        private int registerLimitPerIp = 10;
        private int protectedApiLimitPerIp = 300;
        private long cacheMaxSize = 20000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }

        public int getLoginLimitPerIp() {
            return loginLimitPerIp;
        }

        public void setLoginLimitPerIp(int loginLimitPerIp) {
            this.loginLimitPerIp = loginLimitPerIp;
        }

        public int getRegisterLimitPerIp() {
            return registerLimitPerIp;
        }

        public void setRegisterLimitPerIp(int registerLimitPerIp) {
            this.registerLimitPerIp = registerLimitPerIp;
        }

        public int getProtectedApiLimitPerIp() {
            return protectedApiLimitPerIp;
        }

        public void setProtectedApiLimitPerIp(int protectedApiLimitPerIp) {
            this.protectedApiLimitPerIp = protectedApiLimitPerIp;
        }

        public long getCacheMaxSize() {
            return cacheMaxSize;
        }

        public void setCacheMaxSize(long cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
        }
    }
}

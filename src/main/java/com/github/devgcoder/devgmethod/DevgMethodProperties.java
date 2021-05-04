package com.github.devgcoder.devgmethod;

import com.github.devgcoder.devgmethod.model.Redis;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devg.method")
public class DevgMethodProperties {

    private Redis redis;

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }
}

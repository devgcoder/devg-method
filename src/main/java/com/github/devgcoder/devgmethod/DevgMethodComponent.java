package com.github.devgcoder.devgmethod;

import com.github.devgcoder.devgmethod.model.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class DevgMethodComponent {

    private final Logger logger = LoggerFactory.getLogger(DevgMethodComponent.class);

    private DevgMethodProperties devgMethodProperties;

    public DevgMethodComponent(DevgMethodProperties devgMethodProperties) {
        this.devgMethodProperties = devgMethodProperties;
    }

    @PostConstruct
    public void initDevgMethodComponent() {
        registerDevgMethod();
    }

    private void registerDevgMethod() {
        if (null == devgMethodProperties) {
            logger.error("devg-method properties can not be null");
            return;
        }
        Redis redis = devgMethodProperties.getRedis();
        if (null == redis) {
            logger.error("devg-method redisConfig can not be null");
            return;
        }
        String host = redis.getHost();
        Integer port = redis.getPort();
        Integer timeout = redis.getTimeout();
        Integer database = redis.getDatabase();
        Integer maxIdle = redis.getMaxIdle();
        Integer minIdle = redis.getMinIdle();
        Integer maxTotal = redis.getMaxTotal();
        Long maxWaitMillis = redis.getMaxWaitMillis();
        Boolean testWhileIdle = redis.getTestWhileIdle();
        Boolean testOnBorrow = redis.getTestOnBorrow();
        host = (host == null ? "127.0.0.1" : host);
        port = (port == null ? 6379 : port);
        timeout = (timeout == null ? 3000 : timeout);
        database = (database == null ? 1 : database);
        maxIdle = (maxIdle == null ? 8 : maxIdle);
        minIdle = (minIdle == null ? 0 : minIdle);
        maxTotal = (maxTotal == null ? 500 : maxTotal);
        maxWaitMillis = (maxWaitMillis == null ? 8000 : maxWaitMillis);
        testWhileIdle = (testWhileIdle == null ? true : testWhileIdle);
        testOnBorrow = (testOnBorrow == null ? true : testOnBorrow);
        String password = redis.getPassword();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        JedisPool jedisPool;
        if (null == password || password.trim().equals("")) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        }
        DevgMethodMemory.redisConfigMap.put(DevgMethodMemory.jedisPool, jedisPool);
    }

}
